package com.pyra.krpytapplication.view.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.SharedHelper
import com.pyra.krpytapplication.Utils.openActivity
import com.pyra.krpytapplication.domain.OnClickButtonListener
import com.pyra.krpytapplication.notification.NotificationUtils
import com.pyra.krpytapplication.videocallutils.events.ConstantApp
import com.pyra.krpytapplication.view.fragment.ChatFragment
import com.pyra.krpytapplication.view.fragment.ProfileFragment
import com.pyra.krpytapplication.view.fragment.VaultFragment
import com.pyra.krpytapplication.view.fragment.VaultPassDialogFragment
import com.pyra.krpytapplication.viewmodel.CallViewModel
import com.pyra.krpytapplication.viewmodel.ChatListViewModel
import getImei
import kotlin.properties.Delegates

class MainActivity : BaseActivity(), OnClickButtonListener {
    private val callViewModel: CallViewModel by viewModels()
    var sharedHelper: SharedHelper? = null


    private val chatListViewModel: ChatListViewModel by viewModels()

    private lateinit var profielFragment: ProfileFragment
    private lateinit var vaultFragment: VaultFragment

    private val vaultPassDialogFragment: VaultPassDialogFragment by lazy {
        VaultPassDialogFragment()
    }
    private val chatFragment: ChatFragment by lazy {
        ChatFragment()
    }

    val bottomNavigation: BottomNavigationView by lazy {
        findViewById<BottomNavigationView>(R.id.bottomNavigation)
    }

    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = chatFragment

    var lastSelectedTab: Int by Delegates.observable(R.id.chat) { _, _, _ -> }
    var isVerified = false


    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedHelper = SharedHelper(this)
        updateDeviceToken()

        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fm.beginTransaction().add(R.id.nav_host, chatFragment, "ChatFragment").commit()
        FirebaseAnalytics.getInstance(this)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val intent = Intent()
//            val packageName = packageName
//            val pm = getSystemService(POWER_SERVICE) as PowerManager
//            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
//                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
//                intent.data = Uri.parse("package:$packageName")
//                startActivity(intent)
//            }
//        }


    }


    private fun getGroupDetails() {
        chatListViewModel.getGroupDetails()
        chatListViewModel.getProfileImages()
    }


    override fun onResume() {
        super.onResume()
//        updateLoginTime()
        getGroupDetails()
        NotificationUtils(this).removeNotification()

        askPermissions()

    }

    private fun askPermissions() {


        checkSelfPermission(
            Manifest.permission.RECORD_AUDIO,
            ConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO
        )

        checkSelfPermission(
            Manifest.permission.CAMERA,
            ConstantApp.PERMISSION_REQ_ID_CAMERA
        )

        checkSelfPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
        )
    }

    fun checkSelfPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(permission),
                requestCode
            )
        }
    }

    private fun updateDeviceToken() {
        Log.d("krypt ", sharedHelper?.kryptKey)
        callViewModel.updateDeviceToken(
            "android",
            getImei(this),
            sharedHelper?.firebaseToken!!,
            sharedHelper?.kryptKey
        )
    }

    private val mOnNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.chat -> {
                    fm.beginTransaction().hide(active).show(chatFragment).commit()
                    active = chatFragment
                    lastSelectedTab = R.id.chat
                    return@OnNavigationItemSelectedListener true
                }

                R.id.profile -> {
                    if (this@MainActivity::profielFragment.isInitialized) {
                        fm.beginTransaction().hide(active).show(profielFragment).commit()
                        active = profielFragment
                        lastSelectedTab = R.id.profile
                    } else {
                        profielFragment = ProfileFragment()
                        fm.beginTransaction().add(R.id.nav_host, profielFragment, "ProfielFragment")
                            .hide(active).commit()
                        active = profielFragment
                        lastSelectedTab = R.id.profile
                    }

                    return@OnNavigationItemSelectedListener true
                }


                R.id.vault -> {

                    if(sharedHelper?.vaultPasswordEnabled!!) {
                        if (isVerified) {
                            if (this@MainActivity::vaultFragment.isInitialized) {
                                fm.beginTransaction().hide(active).show(vaultFragment).commit()
                                active = vaultFragment
                                lastSelectedTab = R.id.vault
                            } else {
                                vaultFragment = VaultFragment()
                                fm.beginTransaction()
                                    .add(R.id.nav_host, vaultFragment, "VaultFragment")
                                    .hide(active).commit()
                                active = vaultFragment
                                lastSelectedTab = R.id.vault
                            }

                            isVerified = false

                        } else {
                            if (!vaultPassDialogFragment.isAdded) {
                                vaultPassDialogFragment.show(
                                    supportFragmentManager,
                                    "vaultPassDialogFragment"
                                )
                            }

                        }
                    }else{

                        if (this@MainActivity::vaultFragment.isInitialized) {
                            fm.beginTransaction().hide(active).show(vaultFragment).commit()
                            active = vaultFragment
                            lastSelectedTab = R.id.vault
                        } else {
                            vaultFragment = VaultFragment()
                            fm.beginTransaction()
                                .add(R.id.nav_host, vaultFragment, "VaultFragment")
                                .hide(active).commit()
                            active = vaultFragment
                            lastSelectedTab = R.id.vault
                        }

                    }



                    return@OnNavigationItemSelectedListener true
                }
            }


            return@OnNavigationItemSelectedListener false

        }

    override fun onClickListener() {
        openActivity(ContactActivity::class.java)
    }


}
