package com.pyra.krpytapplication.view.activity

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.Constants
import com.pyra.krpytapplication.Utils.SharedHelper
import com.pyra.krpytapplication.Utils.openNewTaskActivity
import isValidPassword
import kotlinx.android.synthetic.main.activity_create_password.*
import kotlinx.android.synthetic.main.activity_duress_password.*
import showHidePass
import showToast
import java.util.*

class DuressPasswordActivity : BaseActivity() {

    var sharedHelper = SharedHelper(this)

    var isChangePasswordFlow = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duress_password)

        //setting eye color wrt to theme
        if (SharedHelper(this).theme == "light") {
            hideShowDuressPassword.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.dark_page_bg
                )
            )
            hideShowConfirmDuressPassword.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.dark_page_bg
                )
            )
            hideShowVaultPassword.setColorFilter(ContextCompat.getColor(this, R.color.dark_page_bg))
            hideShowConfirmVaultPassword.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.dark_page_bg
                )
            )
        } else {
            hideShowDuressPassword.setColorFilter(ContextCompat.getColor(this, R.color.white))
            hideShowConfirmDuressPassword.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            hideShowVaultPassword.setColorFilter(ContextCompat.getColor(this, R.color.white))
            hideShowConfirmVaultPassword.setColorFilter(ContextCompat.getColor(this, R.color.white))
        }

        intent?.extras?.let {
            isChangePasswordFlow = it.getBoolean(Constants.IntentKeys.CHANGEPASSWORDFLOW)
        }
    }

    fun showHidePass(view: View) {

        when (view.id) {
            R.id.hideShowDuressPassword -> {
                duressPassword.showHidePass(view)
            }
            R.id.hideShowConfirmDuressPassword -> {
                duressConfirmPassword.showHidePass(view)
            }
            R.id.hideShowVaultPassword -> {
                vaultPassword.showHidePass(view)
            }
            R.id.hideShowConfirmVaultPassword -> {
                confirmVaultPassword.showHidePass(view)
            }
        }
    }

    fun onSubmitButtonClicked(view: View) {

        isValidPassword(
            this,
            duressPassword.text.trim().toString(),
            getString(R.string.duress_password)
        ).let { duressResult ->
            if (duressResult != "true") {

                showToast(duressResult)

                return
            }
        }

        isValidPassword(
            this,
            vaultPassword.text.trim().toString(),
            getString(R.string.vault_password)
        ).let { vaultResult ->
            if (vaultResult != "true") {

                showToast(vaultResult)

                return
            }
        }

        when {
            duressPassword.text.trim().toString() == vaultPassword.text.trim().toString() ||
                    duressPassword.text.trim().toString() == sharedHelper.password ||
                    vaultPassword.text.trim().toString() == sharedHelper.password -> {

                showToast(getString(R.string.not_same))

            }
            duressPassword.text.trim().toString() != duressConfirmPassword.text.trim()
                .toString() -> {

                showToast(getString(R.string.retype_duress_password))

            }
            vaultPassword.text.trim().toString() != confirmVaultPassword.text.trim()
                .toString() -> {
                showToast(getString(R.string.retype_vault_password))

            }
            else -> {
                sharedHelper.duressPassword = duressPassword.text.trim().toString()
                sharedHelper.vaultPassword = vaultPassword.text.trim().toString()

                if (isChangePasswordFlow) {
                    finish()
                } else {
                    when (sharedHelper.autoLockType) {
                        "seconds" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.SECOND, sharedHelper.autoLockTime)
                            sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                        }

                        "minutes" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.MINUTE, sharedHelper.autoLockTime)
                            sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                        }

                        "hours" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.HOUR_OF_DAY, sharedHelper.autoLockTime)
                            sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                        }

                        "days" -> {
                            val calendar = Calendar.getInstance()
                            calendar.add(Calendar.DAY_OF_YEAR, sharedHelper.autoLockTime)
                            sharedHelper.autoLogoutSavedTime = "${calendar.time.time}"
                        }

                    }
                    openNewTaskActivity(MainActivity::class.java)
                }
            }
        }

    }
}