package com.pyra.krpytapplication.view.fragment


import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.SharedHelper
import com.pyra.krpytapplication.Utils.openNewTaskActivity
import com.pyra.krpytapplication.view.activity.KryptCodeActivity
import com.pyra.krpytapplication.view.activity.MainActivity
import com.pyra.krpytapplication.viewmodel.ChatListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_password.*
import showToast


class VaultPassDialogFragment : BottomSheetDialogFragment() {


    private val chatListViewModel: ChatListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_password, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        buttonBg.setOnClickListener {


            if (password.text.trim().toString() != "") {
                when {
                    password.text.trim()
                        .toString() == SharedHelper(requireActivity()).vaultPassword -> {
                        (requireActivity() as MainActivity).isVerified = true
                        (requireActivity() as MainActivity).bottomNavigation.selectedItemId =
                            R.id.vault
                        password.setText("")
                        dismiss()
                    }
                    password.text.trim()
                        .toString() == SharedHelper(requireActivity()).duressPassword -> {
                        chatListViewModel.clearDb()
                        chatListViewModel.removeCache()
                        chatListViewModel.clearLocalStorage()
                        requireContext().openNewTaskActivity(KryptCodeActivity::class.java)
                        dismiss()
                    }
                    else -> {

                        dialog?.window?.decorView?.let {

                            requireContext().showToast(requireActivity().getString(R.string.invalid_password))

                        }

                    }
                }

            } else
                dialog?.window?.decorView?.let {
                    requireContext().showToast(requireActivity().getString(R.string.invalid_password))

                }


        }

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (!(requireActivity() as MainActivity).isVerified)
            (requireActivity() as MainActivity).bottomNavigation.selectedItemId =
                (requireActivity() as MainActivity).lastSelectedTab

    }


}