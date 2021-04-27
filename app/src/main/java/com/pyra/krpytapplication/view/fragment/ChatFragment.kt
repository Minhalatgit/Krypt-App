package com.pyra.krpytapplication.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.*
import com.pyra.krpytapplication.domain.OnClickButtonListener
import com.pyra.krpytapplication.view.activity.KryptCodeActivity
import com.pyra.krpytapplication.view.activity.PasswordActivity
import com.pyra.krpytapplication.view.adapter.ChatListAdapter
import com.pyra.krpytapplication.viewmodel.ChatListViewModel
import com.pyra.krpytapplication.viewmodel.ProfileViewModel
import com.pyra.krpytapplication.viewmodel.SearchViewModel
import com.pyra.network.UrlHelper
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment(R.layout.fragment_chat) {

    lateinit var chatListAdapter: ChatListAdapter
    var onClickButtonListener: OnClickButtonListener? = null
    lateinit var chatListViewModel: ChatListViewModel
    lateinit var searchViewModel: SearchViewModel
    var loader: Dialog? = null
    val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatListViewModel = ViewModelProvider(this).get(ChatListViewModel::class.java)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        initAdapter()
        initListener()
        setMenuInflater()
    }

    private fun showSearchLayout() {
        selectionPanel.show()
        toolBar.hide()
    }

    private fun showNormalLayout() {
        selectionPanel.hide()
        toolBar.show()
    }

    private fun initListener() {

        searchText.addTextChangedListener {
            if (it.toString().trim() == "") {
                chatListViewModel.getChatListAsync()
            } else {
                chatListViewModel.getSearchedData(it.toString())
            }
        }

        backPressed.setOnClickListener {
            chatListViewModel.removeSelection()
        }

        deleteChat.setOnClickListener {
            showRemoveDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showRemoveDialog() {
        val dialog = getChatDeleteDialog(requireContext())

        val title = dialog.findViewById<TextView>(R.id.title)
        val checkBox = dialog.findViewById<CheckBox>(R.id.checkBox)
        val cancel = dialog.findViewById<TextView>(R.id.cancel)
        val delete = dialog.findViewById<TextView>(R.id.delete)

        if (chatListViewModel.getSelectedCount() == 1) {
            title.text =
                getString(R.string.delete_chat_with) + " \"" + chatListViewModel.selectedRoomName + "\""
        } else {
            title.text = "Delete " + chatListViewModel.getSelectedCount() + " selected chats"
        }

        cancel.setOnClickListener {
            dialog.dismiss()
        }

        delete.setOnClickListener {
            chatListViewModel.deleteSelectedChats(checkBox.isChecked)
            dialog.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        chatListViewModel.checkForBurnMessage()
        println("LXMPP On Resume Called")

        checkSubscription()
    }

    private fun checkSubscription() {

        profileViewModel.getUserDeatilsResponse(
            SharedHelper(requireContext()).kryptKey,
            UrlHelper.GETUSERDETAILS
        )
            .observe(viewLifecycleOwner) {

                if (it.error == "false") {

                    it.data[0].subsEnddate?.let {

                        val endDatedate = getFormatedDate(
                            it,
                            "yyyy-MM-dd'T'HH:mm:ss",
                            "yyyy-MM-dd"
                        )

                        endDatedate?.let {
                            if (endDatedate != "Not Updated") {
                                var isSubscriptionEnded =
                                    isSubScriptionEnded(endDatedate, "yyyy-MM-dd")

                                if (isSubscriptionEnded) {
                                    clearAllData()
                                }
                            }
                        }

                    }

                }

            }

    }

    private fun clearAllData() {

        val bundle = Bundle()
        bundle.putBoolean("isSubEnded", true)
        requireActivity().openNewTaskActivity(KryptCodeActivity::class.java, bundle)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onClickButtonListener = (activity as OnClickButtonListener?)

    }

    private fun initAdapter() {
        val linearLayoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        chatListAdapter = ChatListAdapter(requireActivity(), chatListViewModel)
        chatLists.layoutManager = linearLayoutManager
        chatLists.adapter = chatListAdapter
        listener()

        chatListViewModel.update.observe(viewLifecycleOwner) {
            chatListAdapter.notifyDataSetChanged()
            if (chatListViewModel.isMultiSelectionEnabled) {
                showSearchLayout()
            } else {
                showNormalLayout()
            }
            selectedCount.text = chatListViewModel.getSelectedCount().toString()
        }

        chatListViewModel.getChatList()

    }

    private fun listener() {
        contactButton.setOnClickListener {
            onClickButtonListener?.onClickListener()
        }
        newUser.setOnClickListener {
            findNavController().navigate(ChatFragmentDirections.actionChatToAddContactDialog())

            //Todo Change bottom sheet to dialog box
//            val dialogView = View.inflate(requireContext(), R.layout.dialog_enter_krypt_code, null)
//            val dialog = Dialog(requireContext())
//            val kryptCodeEditText = dialogView.findViewById<EditText>(R.id.kryptCode)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.setContentView(dialogView)
//            val window: Window = dialog.window!!
//            window.setGravity(Gravity.CENTER)
//            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//            dialog.show()
//
//            val backButton = dialog.findViewById<ImageView>(R.id.backIcon)
//            backButton.setOnClickListener { dialog.dismiss() }
//
//            val submitButton = dialog.findViewById<TextView>(R.id.submitButton)
//            submitButton.setOnClickListener {
//                val kryptCode = kryptCodeEditText.text.toString()
//                if (isValidKryptCode(kryptCode)) {
//                    dialog.dismiss()
//                    val intent = Intent(this, PasswordActivity::class.java)
//                    intent.putExtra("isBackEnabled", true)
//                    intent.putExtra("kryptCode", kryptCode)
//                    openActivity(intent)
//                } else {
//                    print("Failed")
//
//                    showToast(getString(R.string.invalid_krypt_code))
//                }
//            }
        }
    }

    private fun setMenuInflater() {

        val popup = PopupMenu(requireContext(), selectionMenu)
        popup.menuInflater.inflate(R.menu.chat_message_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.clearChat -> {
                    chatListViewModel.clearAllChats()
                }
            }
            return@setOnMenuItemClickListener true
        }

        selectionMenu.setOnClickListener {
            popup.show()
        }

    }
}
