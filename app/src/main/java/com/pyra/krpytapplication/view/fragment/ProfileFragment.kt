package com.pyra.krpytapplication.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amitshekhar.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.*
import com.pyra.krpytapplication.view.activity.*
import com.pyra.krpytapplication.view.adapter.BurnMsgUnitAdapter
import com.pyra.krpytapplication.view.adapter.CountAdapter
import com.pyra.krpytapplication.viewmodel.AmazonViewModel
import com.pyra.krpytapplication.viewmodel.ChatListViewModel
import com.pyra.krpytapplication.viewmodel.ProfileViewModel
import com.pyra.network.UrlHelper
import fetchThemeColor
import getImei
import kotlinx.android.synthetic.main.fragment_profile.*
import showToast
import java.io.File
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    var uploadFile: File? = null
    private val sharedHelper: SharedHelper by lazy {
        SharedHelper(requireContext())
    }
    var amazonViewModel: AmazonViewModel? = null
    val profileViewModel: ProfileViewModel by viewModels()
    val chatListViewModel: ChatListViewModel by viewModels()

    var minutes: TextView? = null
    var hours: TextView? = null
    var days: TextView? = null
    var done: TextView? = null
    var seconds: TextView? = null
    lateinit var viewPager: RecyclerViewPager
    lateinit var unitViewPager: RecyclerViewPager
    var selectedType = MessageBurnType.MINUTES.toString()
    var counterAdapter: CountAdapter? = null
    private lateinit var timer: CountDownTimer
    lateinit var burnMsgUnitAdapter: BurnMsgUnitAdapter
    var dialogTitle: TextView? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        amazonViewModel = ViewModelProvider(this).get(AmazonViewModel::class.java)

        initListners()
        setMessageTime()

        profileViewModel.getUserDeatilsResponse(sharedHelper.kryptKey, UrlHelper.GETUSERDETAILS)
            .observe(viewLifecycleOwner) {

                if (it.error == "false") {
                    it.data[0].subsStartDate?.let {
                        startDate.text = getFormatedDate(
                            it,
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "yyyy-MM-dd"
                        )
                    }

                    it.data[0].subsEnddate?.let {
                        endDate.text = getFormatedDate(
                            it,
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "yyyy-MM-dd"
                        )
                    }

                }

            }

    }

    private fun initView() {
        val sharedHelper = SharedHelper(requireActivity())
        userName.text = sharedHelper.kryptKey
        userStatus.text = sharedHelper.status
        kryptCode.text = sharedHelper.kryptKey
        currentStatus.text = sharedHelper.status

        darkTheme.isChecked = sharedHelper.theme == "light"

        darkTheme.setOnCheckedChangeListener { _, b ->
            if (b)
                sharedHelper.theme = "light"
            else
                sharedHelper.theme = "dark"

            context?.openNewTaskActivity(MainActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        initView()
        countDown()
    }

    private fun initListners() {

        enableVaultPassword.isChecked = sharedHelper.vaultPasswordEnabled
        burnDisableSwitch.isChecked = sharedHelper.isBurnMessageEnabled

        changeThemeView.setOnClickListener {
//            getChangeThemeDialog(requireContext()) {
//                requireContext().openNewTaskActivity(MainActivity::class.java)
//            }
        }

        enableVaultPassword.setOnCheckedChangeListener { p0, p1 ->
            sharedHelper.vaultPasswordEnabled = p1
        }

        burnDisableSwitch.setOnCheckedChangeListener { p0, p1 ->
            sharedHelper.isBurnMessageEnabled = p1
            chatListViewModel.updateLoginTime()
        }

        editIcon.setOnClickListener {
            moveStatusActivity()
        }

        editProfile.setOnClickListener {
            openGallery()
        }

        profileViewModel.getProperties(getImei(requireContext()))

        profileViewModel.profileImage.observe(viewLifecycleOwner, Observer {

            userImage.loadImage(it)

        })

        profileViewModel.status.observe(viewLifecycleOwner, Observer {
            currentStatus.text = sharedHelper.status

        })


        profileViewModel.success.observe(viewLifecycleOwner, Observer {
            if (it) {
                progressBar.visibility = View.GONE
            }
        })


        editNameIcon.setOnClickListener {
            showEditNameDialog()
        }

        changePasswordView.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        changeChatThemeView.setOnClickListener {
            val intent = Intent(requireContext(), ChangeChatThemeActivity::class.java)
            startActivity(intent)
        }

        burnMessageView.setOnClickListener {
            showBurnMessageDialog()
        }

        blockedView.setOnClickListener {
            context?.openActivity(BlockedListActivity::class.java)
        }

        copyCode.setOnClickListener {
            val clipboard: ClipboardManager =
                requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Code Copied", kryptCode.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Code Copied", Toast.LENGTH_SHORT).show()
        }

        deleteView.setOnClickListener {

            deleteAllChat(requireContext()) {
                profileViewModel.deleteAllChatMessages()
            }
        }


        lockView.setOnClickListener {
            val dialog = getMessageBurnDialog(requireContext())
            done = dialog.findViewById(R.id.done)
            dialogTitle = dialog.findViewById(R.id.dialog_title)
            viewPager = dialog.findViewById(R.id.timeSelector)
            unitViewPager = dialog.findViewById(R.id.unitSelector)

            setDialogTitle("Auto Logout")


            // minutes = dialog.findViewById(R.id.minutes)
            // hours = dialog.findViewById(R.id.hours)
            //   days = dialog.findViewById(R.id.days)
            //   seconds = dialog.findViewById(R.id.seconds)


            /* minutes?.setOnClickListener {

                 changeView(MessageBurnType.MINUTES.type)
             }

             hours?.setOnClickListener {

                 changeView(MessageBurnType.HOURS.type)
             }

             days?.setOnClickListener {

                 changeView(MessageBurnType.DAYS.type)
             }

             seconds?.setOnClickListener {

                 changeView(MessageBurnType.SECONDS.type)
             }*/

            done?.setOnClickListener {

                selectedType =
                    burnMsgUnitAdapter.unitList.get(unitViewPager.currentPosition).toLowerCase()
                sharedHelper.autoLockType = selectedType
                sharedHelper.autoLockTime = viewPager.currentPosition + 1
                profileViewModel.updateLoginTime()
                autoLockTime()
                dialog.dismiss()
            }
            // changeView(MessageBurnType.SECONDS.type)
            initBurnMessageScrollview()
        }
    }

    private fun showBurnMessageDialog() {
        val dialog = getMessageBurnDialog(requireContext())
        done = dialog.findViewById(R.id.done)
        viewPager = dialog.findViewById(R.id.timeSelector)
        unitViewPager = dialog.findViewById(R.id.unitSelector)


        //  minutes = dialog.findViewById(R.id.minutes)
        //   hours = dialog.findViewById(R.id.hours)
        //   days = dialog.findViewById(R.id.days)
        //  seconds = dialog.findViewById(R.id.seconds)


        /*  minutes?.setOnClickListener {

              changeView(MessageBurnType.MINUTES.type)
          }*/

/*
        hours?.setOnClickListener {

            changeView(MessageBurnType.HOURS.type)
        }
*/

/*
        days?.setOnClickListener {

            changeView(MessageBurnType.DAYS.type)
        }
*/

/*
        seconds?.setOnClickListener {

            changeView(MessageBurnType.SECONDS.type)
        }
*/

        done?.setOnClickListener {
            selectedType =
                burnMsgUnitAdapter.unitList.get(unitViewPager.currentPosition).toLowerCase()
            sharedHelper.burnMessageType = selectedType
            sharedHelper.burnMessageTime = viewPager.currentPosition + 1
            setMessageTime()
            profileViewModel.updateLoginTime()
            dialog.dismiss()
        }

        //  changeView(MessageBurnType.SECONDS.type)
        initBurnMessageScrollview()
    }

    fun changeView(type: String) {

        selectedType = type
        when (type) {
            MessageBurnType.MINUTES.type -> {
                minutes?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selected_burntime_bg)

                hours?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                days?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                seconds?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                seconds?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )

                minutes?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                hours?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )
                days?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )


            }
            MessageBurnType.HOURS.type -> {
                hours?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selected_burntime_bg)

                minutes?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                days?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                seconds?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                seconds?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )

                hours?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                minutes?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )
                days?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )
            }
            MessageBurnType.DAYS.type -> {
                days?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selected_burntime_bg)

                minutes?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                hours?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )

                seconds?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                seconds?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )


                days?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                minutes?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )
                hours?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )


            }
            MessageBurnType.SECONDS.type -> {
                seconds?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selected_burntime_bg)

                days?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                minutes?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )
                hours?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.transparent
                    )
                )

                seconds?.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )

                days?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )

                minutes?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )
                hours?.setTextColor(
                    fetchThemeColor(R.attr.text_color_content, requireContext())
                )

            }
        }

        setViewPager()
        setUnitViewPager()
    }

    private fun setViewPager() {

        viewPager.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        counterAdapter = CountAdapter(requireContext(), selectedType)

        viewPager.adapter = counterAdapter

        viewPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView, i: Int, i2: Int) {
                Log.d("Value", "SCroll Called")

                val childCount = viewPager.childCount
                val width = viewPager.getChildAt(0).width
                val padding = (viewPager.width - width) / 2

                for (j in 0 until childCount) {
                    val v = recyclerView.getChildAt(j)
                    var rate = 0f
                    if (v.left <= padding) {
                        rate = if (v.left >= padding - v.width) {
                            (padding - v.left) * 1f / v.width
                        } else {
                            1f
                        }
                        v.scaleY = 1 - rate * 0.1f
                        v.scaleX = 1 - rate * 0.1f

                    } else {
                        if (v.left <= recyclerView.width - padding) {
                            rate = (recyclerView.width - padding - v.left) * 1f / v.width
                        }
                        v.scaleY = 0.9f + rate * 0.1f
                        v.scaleX = 0.9f + rate * 0.1f
                    }
                }
            }
        })
        viewPager.addOnPageChangedListener(
            RecyclerViewPager.OnPageChangedListener
            { oldPosition, newPosition ->
                Log.d(
                    "test",
                    "oldPosition:$oldPosition newPosition:$newPosition "

                )
            })

        viewPager.addOnLayoutChangeListener(View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            Log.d("Value", "Called")
            if (viewPager.childCount < 3) {
                if (viewPager.getChildAt(1) != null) {
                    if (viewPager.currentPosition == 0) {
                        val v1 = viewPager.getChildAt(1)
                        v1.scaleY = 0.9f
                        v1.scaleX = 0.9f
                    } else {
                        val v1 = unitViewPager.getChildAt(0)
                        v1.scaleY = 0.9f
                        v1.scaleX = 0.9f
                    }
                }
            } else {
                if (viewPager.getChildAt(0) != null) {
                    val v0 = viewPager.getChildAt(0)
                    v0.scaleY = 0.9f
                    v0.scaleX = 0.9f
                }
                if (viewPager.getChildAt(2) != null) {
                    val v2 = viewPager.getChildAt(2)
                    v2.scaleY = 0.9f
                    v2.scaleX = 0.9f
                }
            }

        })

    }

    private fun setUnitViewPager() {

        unitViewPager.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        burnMsgUnitAdapter = BurnMsgUnitAdapter(requireContext())
        unitViewPager.adapter = burnMsgUnitAdapter

        unitViewPager.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, scrollState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView, i: Int, i2: Int) {
                Log.d("Value", "SCroll Called")
                val childCount = unitViewPager.childCount
                val width = unitViewPager.getChildAt(0).width
                val padding = (unitViewPager.width - width) / 2

                for (j in 0 until childCount) {
                    val v = recyclerView.getChildAt(j)
                    var rate = 0f
                    if (v.left <= padding) {
                        rate = if (v.left >= padding - v.width) {
                            (padding - v.left) * 1f / v.width
                        } else {
                            1f
                        }
                        v.scaleY = 1 - rate * 0.1f
                        v.scaleX = 1 - rate * 0.1f

                    } else {
                        if (v.left <= recyclerView.width - padding) {
                            rate = (recyclerView.width - padding - v.left) * 1f / v.width
                        }
                        v.scaleY = 0.9f + rate * 0.1f
                        v.scaleX = 0.9f + rate * 0.1f
                    }
                }
            }
        })
        unitViewPager.addOnPageChangedListener(
            RecyclerViewPager.OnPageChangedListener
            { oldPosition, newPosition ->
                Log.d(
                    "test",
                    "oldPosition:$oldPosition newPosition:$newPosition"
                )
            })

        unitViewPager.addOnLayoutChangeListener(View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            Log.d("Value", "Called")
            if (unitViewPager.childCount < 3) {
                if (unitViewPager.getChildAt(1) != null) {
                    if (unitViewPager.currentPosition == 0) {
                        val v1 = unitViewPager.getChildAt(1)
                        v1.scaleY = 0.9f
                        v1.scaleX = 0.9f
                    } else {
                        val v1 = unitViewPager.getChildAt(0)
                        v1.scaleY = 0.9f
                        v1.scaleX = 0.9f
                    }
                }
            } else {
                if (unitViewPager.getChildAt(0) != null) {
                    val v0 = unitViewPager.getChildAt(0)
                    v0.scaleY = 0.9f
                    v0.scaleX = 0.9f
                }
                if (unitViewPager.getChildAt(2) != null) {
                    val v2 = unitViewPager.getChildAt(2)
                    v2.scaleY = 0.9f
                    v2.scaleX = 0.9f
                }
            }

        })

    }

    private fun showEditNameDialog() {
        val dialogView = View.inflate(activity, R.layout.dialog_edit_name, null)
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        val window: Window = dialog.window!!
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.show()
        val doneIcon = dialog.findViewById<ImageView>(R.id.doneIcon)
        doneIcon.setOnClickListener {
            dialog.dismiss()
        }

    }

    private fun showEditImageDialog() {
        val dialogView = View.inflate(activity, R.layout.dialog_edit_profile_image, null)
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        val window: Window = dialog.window!!
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    private fun moveStatusActivity() {
        val intent = Intent(context, ChooseStatusActivity::class.java)
        requireActivity().startActivity(intent)
    }


    override fun handleGallery(data: Intent?) {
        if (data != null) {
            data.getStringExtra("imageUrl")?.let { mediaUrl ->
                Log.d("ImageUrl", mediaUrl)
                profileViewModel.updateImage(mediaUrl)
                userImage.loadImage(mediaUrl)
                sharedHelper.imageUrlPath = mediaUrl

            }

        }
    }

    override fun handleCamera(file: File) {
//        uploadFile = File(sharedHelper!!.imageUploadPath)
        if (file.exists()) {

            sharedHelper.imagePath = file.absolutePath
            userImage.loadImage(file.absolutePath)
            uploadFileToAws(file)
        } else {
            requireContext().showToast(getString(R.string.unable_to_retrieve))

        }
    }


    private fun uploadFileToAws(file: File?) {

        file?.let { it ->
            progressBar.visibility = View.VISIBLE
            amazonViewModel?.uploadImage(requireContext(), it)
        }

        amazonViewModel?.amazonUrl?.observe(this, Observer {
            if (it != "") {
                profileViewModel.updateImage(it)
                sharedHelper.imageUrlPath = it
            }
        })

        amazonViewModel?.errorMessage?.observe(viewLifecycleOwner, Observer {
            progressBar.visibility = View.GONE
            if (it != "") {
                requireContext().showToast(it)

            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun setMessageTime() {

        messageBurnTime.text =
            sharedHelper.burnMessageTime.toString() + " " + sharedHelper.burnMessageType.toString()
    }


    @SuppressLint("SetTextI18n")
    private fun autoLockTime() {
        if (sharedHelper.autoLockTime < 20 && sharedHelper.autoLockType == "seconds") {
            Toast.makeText(
                requireActivity(),
                getString(R.string.PF_toast_auotlock),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            when (sharedHelper.autoLockType) {
                "seconds" -> {
                    timer.cancel()
                    (requireActivity() as BaseActivity).timerBase.cancel()
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.SECOND, sharedHelper.autoLockTime)
                    sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                    countDown()
                    (requireActivity() as BaseActivity).countDownForAutoLogout()
                }

                "minutes" -> {
                    timer.cancel()
                    (requireActivity() as BaseActivity).timerBase.cancel()
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.MINUTE, sharedHelper.autoLockTime)
                    sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                    countDown()
                    (requireActivity() as BaseActivity).countDownForAutoLogout()
                }

                "hours" -> {
                    timer.cancel()
                    (requireActivity() as BaseActivity).timerBase.cancel()
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.HOUR_OF_DAY, sharedHelper.autoLockTime)
                    sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                    countDown()
                    (requireActivity() as BaseActivity).countDownForAutoLogout()
                }

                "days" -> {
                    timer.cancel()
                    (requireActivity() as BaseActivity).timerBase.cancel()
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, sharedHelper.autoLockTime)
                    sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                    countDown()
                    (requireActivity() as BaseActivity).countDownForAutoLogout()
                }
            }
            Log.e(
                "time",
                sharedHelper.autoLockTime.toString() + " " + sharedHelper.autoLockType.toString()
            )
        }


    }


    private fun countDown() {

        val duration =
            sharedHelper.autoLogoutSavedTime.toLong().minus(Calendar.getInstance().time.time)
        timer = object : CountDownTimer(duration, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli
                autoLockTxtTime.text =
                    "$elapsedDays days $elapsedHours hs $elapsedMinutes min $elapsedSeconds sec"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                autoLockTxtTime.text = "done!"
            }
        }.start()

    }


    override fun onPause() {
        super.onPause()
        if (this::timer.isInitialized) {


            timer.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::timer.isInitialized) {
            timer.cancel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::timer.isInitialized) {
            timer.cancel()
        }

    }

    override fun onDetach() {
        super.onDetach()
        if (this::timer.isInitialized) {
            timer.cancel()
        }
    }


    private fun initBurnMessageScrollview() {
        selectedType = MessageBurnType.SECONDS.type
        setViewPager()
        setUnitViewPager()
    }


    private fun setDialogTitle(text: String) {
        dialogTitle?.text = text
    }


}



