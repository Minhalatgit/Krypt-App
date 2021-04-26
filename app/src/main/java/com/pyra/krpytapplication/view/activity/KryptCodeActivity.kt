package com.pyra.krpytapplication.view.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.openActivity
import com.pyra.krpytapplication.Utils.subEnded
import com.pyra.krpytapplication.viewmodel.ChatListViewModel
import isValidKryptCode
import showToast

class KryptCodeActivity : BaseActivity() {

    var imeManager: InputMethodManager? = null
    val kryptKeyboard =
        "com.krypt.chat/com.pyra.krpytapplication.incognitokeyboard.KryptIncognitoKeyboard"

    val chatListViewModel: ChatListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_krypt_code)
        checkKryptKeyboardEnabled()

        getIntentValues()

    }

    private fun getIntentValues() {

        intent.extras?.getBoolean("isSubEnded")?.let {
            if (it) {
                showSubEbdedDialog()
                deleteLocalStorage()
            }
        }
    }

    private fun deleteLocalStorage() {

        chatListViewModel.clearDb()
        chatListViewModel.removeCache()
        chatListViewModel.clearLocalStorage()

    }

    private fun showSubEbdedDialog() {
        subEnded(this)
    }

    fun checkKryptKeyboardEnabled() {
        imeManager = applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val list: String = imeManager!!.getEnabledInputMethodList().toString()
        if (!list.contains(kryptKeyboard)) {
            startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
        }
    }

    fun enableKryptKeyboard() {
        val defaultKeyboard =
            Settings.Secure.getString(this.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)

        if (!defaultKeyboard.equals(kryptKeyboard, ignoreCase = true)) {
            imeManager!!.showInputMethodPicker()
        }
    }

    fun onGenerateButtonClicked(view: View) {
        enableKryptKeyboard()

        openActivity(CreatePasswordActivity::class.java)
    }

    fun onKryptButtonClicked(view: View) {
        enableKryptKeyboard()

        showKryptCodeDialog()
    }

    private fun showKryptCodeDialog() {
        val dialogView = View.inflate(this, R.layout.dialog_enter_krypt_code, null)
        val dialog = Dialog(this)
        val kryptCodeEditText = dialogView.findViewById<EditText>(R.id.kryptCode)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        val window: Window = dialog.window!!
        window.setGravity(Gravity.BOTTOM)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()

        val backButton = dialog.findViewById<ImageView>(R.id.backIcon)
        backButton.setOnClickListener { dialog.dismiss() }

        val submitButton = dialog.findViewById<TextView>(R.id.submitButton)
        submitButton.setOnClickListener {
            val kryptCode = kryptCodeEditText.text.toString()
            if (isValidKryptCode(kryptCode)) {
                dialog.dismiss()
                val intent = Intent(this, PasswordActivity::class.java)
                intent.putExtra("isBackEnabled", true)
                intent.putExtra("kryptCode", kryptCode)
                openActivity(intent)
            } else {
                print("Failed")

                showToast(getString(R.string.invalid_krypt_code))
            }
        }

    }

}
