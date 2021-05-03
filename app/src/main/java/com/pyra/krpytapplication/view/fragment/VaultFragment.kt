package com.pyra.krpytapplication.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.*
import com.pyra.krpytapplication.databinding.FragmentVaultBinding
import com.pyra.krpytapplication.roomDb.AppDataBase
import com.pyra.krpytapplication.roomDb.entity.ChatMessagesSchema
import com.pyra.krpytapplication.view.activity.CameraActivity
import com.pyra.krpytapplication.view.activity.DocumentActivity
import com.pyra.krpytapplication.view.activity.ForwardActivity
import com.pyra.krpytapplication.view.adapter.ChatMessageAdapter
import com.pyra.krpytapplication.view.adapter.DocumentListAdapter
import com.pyra.krpytapplication.view.adapter.ImageViewAdapter
import com.pyra.krpytapplication.viewmodel.ChatMessagesViewModel
import com.pyra.krpytapplication.viewmodel.VaultFragViewModel
import fetchThemeColor
import kotlinx.coroutines.launch
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class VaultFragment : Fragment() {

    lateinit var fragmentVaultBinding: FragmentVaultBinding
    lateinit var imageViewAdapter: ImageViewAdapter
    lateinit var videoViewAdapter: ImageViewAdapter
    lateinit var chatMessageAdapter: ChatMessageAdapter
    lateinit var documentListAdapter: DocumentListAdapter
    lateinit var topToolbarLayout: Group

    var currentTab = 0

    private val viewModel: VaultFragViewModel by viewModels()

    private val sharedHelper by lazy {
        SharedHelper(requireActivity())
    }

    private val database by lazy {
        AppDataBase.getInstance(requireActivity())?.chatMessagesDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentVaultBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_vault, container, false)
        topToolbarLayout = fragmentVaultBinding.topToolbarLayout
        setImageAdapter()
        initListners()
        setGallerySelected()
        setImageSelected()

        fragmentVaultBinding.forward.setOnClickListener {
            val bundle = Bundle()
            when (currentTab) {
                0 -> bundle.putStringArrayList("list", viewModel.selectedImageList)
                1 -> bundle.putStringArrayList("list", viewModel.selectedVideoList)
                2 -> bundle.putStringArrayList("list", viewModel.selectedDocumentList)
            }
            requireContext().openActivity(ForwardActivity::class.java, bundle)

        }
        handlingFab()

        return fragmentVaultBinding.root
    }

    private fun handlingFab() {
        if (currentTab == 0 || currentTab == 1) fragmentVaultBinding.cameraFAB.setImageResource(R.drawable.ic_camera_alt_24)
        else fragmentVaultBinding.cameraFAB.setImageResource(R.drawable.add_doc)
    }

    private fun initListners() {
        fragmentVaultBinding.savedText.setOnClickListener {
            setSavedSelected()
        }
        fragmentVaultBinding.galleryText.setOnClickListener {
            setGallerySelected()
        }

        fragmentVaultBinding.imageText.setOnClickListener {
            setImageSelected()
        }

        fragmentVaultBinding.videoText.setOnClickListener {
            setVideoSelected()
        }

        fragmentVaultBinding.docText.setOnClickListener {
            setDocSelected()
        }

        fragmentVaultBinding.backPressed.setOnClickListener {
            viewModel.removeSelection()
        }

        fragmentVaultBinding.delete.setOnClickListener {
            viewModel.deleteSelectedItem()
        }

        fragmentVaultBinding.cameraFAB.clickWithDebounce {
            if (currentTab == 0 || currentTab == 1) {
                Intent(requireActivity(), CameraActivity::class.java).apply {
                    putExtra(Constants.IntentKeys.ISVIDEOAVAILABLE, true)
                    putExtra(Constants.IntentKeys.ISUPLOADAVAILABLE, true)
                    putExtra(Constants.IntentKeys.ISVAULT, true)
                    startActivityForResult(this, Constants.RequestCode.CAMERA_INTENT)
                }
            } else {
                startActivityForResult(
                    Intent(requireContext(), DocumentActivity::class.java)
                        .putExtra("isNewDoc", true)
                        .putExtra("path", ""), Constants.RequestCode.DOC_INTENT
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.RequestCode.CAMERA_INTENT -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        if (!it.getStringExtra(Constants.IntentKeys.FILE).isNullOrEmpty()) {
                            if (it.getBooleanExtra(Constants.IntentKeys.ISVIDEOAVAILABLE, false)) {
                                it.getStringExtra(Constants.IntentKeys.FILE)?.let { _ ->
                                    it.getStringExtra(Constants.IntentKeys.FILE)?.let { file ->
                                        with(ChatMessagesSchema()) {
                                            messageId = UUID.randomUUID().toString()
                                            message = ""
                                            messageType = MessageType.VIDEO.toMessageString()
                                            messageStatus = ""
                                            messageTime = System.currentTimeMillis().toString()
                                            isSender = false
                                            roomId = ""
                                            isDeleted = false
                                            isEdited = false
                                            isUploaded = true
                                            kryptId = ""
                                            userImage = ""
                                            userName = ""
                                            mediaUrl =
                                                data.getStringExtra(Constants.IntentKeys.MEDIAURL)
                                                    ?: ""
                                            mediaThumbUrl =
                                                data.getStringExtra(Constants.IntentKeys.THUMBURL)
                                                    ?: ""
                                            localMediaPath = file
                                            lifecycleScope.launch {
                                                database?.insert(this@with)
                                            }

                                        }
                                    }
                                    viewModel.getDownloadedVideoList()

                                }
                            } else {
                                it.getStringExtra(Constants.IntentKeys.FILE)?.let { file ->
                                    with(ChatMessagesSchema()) {
                                        messageId = UUID.randomUUID().toString()
                                        message = ""
                                        messageType = MessageType.IMAGE.toMessageString()
                                        messageStatus = ""
                                        messageTime = System.currentTimeMillis().toString()
                                        isSender = false
                                        roomId = ""
                                        isDeleted = false
                                        isEdited = false
                                        isUploaded = true
                                        kryptId = ""
                                        userImage = ""
                                        userName = ""
                                        mediaUrl =
                                            data.getStringExtra(Constants.IntentKeys.MEDIAURL) ?: ""
                                        mediaThumbUrl =
                                            data.getStringExtra(Constants.IntentKeys.THUMBURL) ?: ""
                                        localMediaPath = file
                                        lifecycleScope.launch {
                                            database?.insert(this@with)
                                        }

                                    }
                                }
                                viewModel.getDownloadedImageList()
                            }
                        }
                    }
                }
            }
            Constants.RequestCode.DOC_INTENT -> setDocSelected()
        }
    }

    private fun setDocSelected() {

        currentTab = 2
        fragmentVaultBinding.docText.activeText()
        fragmentVaultBinding.videoText.inActiveText()
        fragmentVaultBinding.imageText.inActiveText()
        setDocumentAdapter()
        handlingFab()
    }

    private fun setVideoSelected() {

        currentTab = 1
        fragmentVaultBinding.docText.inActiveText()
        fragmentVaultBinding.videoText.activeText()
        fragmentVaultBinding.imageText.inActiveText()
        setVideoAdapter()
        handlingFab()

    }

    private fun setImageSelected() {

        currentTab = 0
        fragmentVaultBinding.docText.inActiveText()
        fragmentVaultBinding.videoText.inActiveText()
        fragmentVaultBinding.imageText.activeText()
        setImageAdapter()
        handlingFab()
    }

    private fun setGallerySelected() {
        fragmentVaultBinding.galleryText.activeText()
        fragmentVaultBinding.savedText.inActiveText()
        fragmentVaultBinding.gallerySelectedView.show()
        fragmentVaultBinding.savedSelectedView.hide()
        topToolbarLayout.show()
        setImageSelected()
    }

    private fun setSavedSelected() {
        fragmentVaultBinding.galleryText.inActiveText()
        fragmentVaultBinding.savedText.activeText()
        fragmentVaultBinding.gallerySelectedView.hide()
        fragmentVaultBinding.savedSelectedView.show()
        setChatMessageAdapter()
        topToolbarLayout.hide()
    }

    private fun setImageAdapter() {

        val linearLayoutManager = GridLayoutManager(activity, 3)
        imageViewAdapter = ImageViewAdapter(requireActivity(), false, viewModel)
        fragmentVaultBinding.imageRecyclerView.layoutManager = linearLayoutManager
        fragmentVaultBinding.imageRecyclerView.adapter = imageViewAdapter

        viewModel.notifyItemImage.observe(viewLifecycleOwner, Observer {
            fragmentVaultBinding.imageRecyclerView.recycledViewPool.clear()
            imageViewAdapter.notifyDataSetChanged()
            checkForDeletePannel()
        })

        viewModel.getDownloadedImageList()
    }

    override fun onResume() {
        super.onResume()

        if (currentTab == 0) {
            setImageAdapter()
        }
    }

    private fun checkForDeletePannel() {

        viewModel.showDeleteView().let {
            if (it) {
                fragmentVaultBinding.constraintLayout.hide()
                fragmentVaultBinding.selectionPanel.show()
            } else {
                fragmentVaultBinding.constraintLayout.show()
                fragmentVaultBinding.selectionPanel.hide()
            }

            fragmentVaultBinding.selectedCount.text = viewModel.getSelectedCount()
        }

    }

    private fun setVideoAdapter() {
        val linearLayoutManager = GridLayoutManager(activity, 3)
        videoViewAdapter = ImageViewAdapter(requireActivity(), true, viewModel)
        fragmentVaultBinding.imageRecyclerView.layoutManager = linearLayoutManager
        fragmentVaultBinding.imageRecyclerView.adapter = videoViewAdapter

        viewModel.notifyItemVideo.observe(viewLifecycleOwner, Observer {
            fragmentVaultBinding.imageRecyclerView.recycledViewPool.clear()
            videoViewAdapter.notifyDataSetChanged()
            checkForDeletePannel()
        })

        viewModel.getDownloadedVideoList()
    }

    private fun setChatMessageAdapter() {

        val chatMessagesViewModel = ViewModelProvider(this).get(ChatMessagesViewModel::class.java)
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        chatMessageAdapter = ChatMessageAdapter(requireActivity(), true, chatMessagesViewModel)
        fragmentVaultBinding.imageRecyclerView.layoutManager = linearLayoutManager
        fragmentVaultBinding.imageRecyclerView.adapter = chatMessageAdapter

        chatMessagesViewModel.update.observe(viewLifecycleOwner, Observer {
//            chatMessageAdapter.notifyDataSetChanged()
            chatMessageAdapter.notifySelection()
        })

        chatMessagesViewModel.getSavedMessages()
        chatMessagesViewModel.notifySelection.observe(viewLifecycleOwner, Observer {
            chatMessageAdapter.notifyDataSetChanged()
            setSelectionPannel(chatMessagesViewModel)
        })

        fragmentVaultBinding.backPressedsaved.setOnClickListener {
            chatMessagesViewModel.unselectAll()
        }

        fragmentVaultBinding.unsave.setOnClickListener {
            chatMessagesViewModel.unsaveSelected()
        }

    }

    private fun setSelectionPannel(chatMessagesViewModel: ChatMessagesViewModel) {

        if (chatMessagesViewModel.selectedChatMessage.size != 0) {
            fragmentVaultBinding.selectionPanelSaved.show()
            fragmentVaultBinding.selectedCountsaved.text =
                chatMessagesViewModel.selectedChatMessage.size.toString()
        } else {
            fragmentVaultBinding.selectionPanelSaved.hide()
        }
    }

    private fun setDocumentAdapter() {

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        documentListAdapter = DocumentListAdapter(this, requireActivity(), viewModel)
        fragmentVaultBinding.imageRecyclerView.layoutManager = linearLayoutManager
        fragmentVaultBinding.imageRecyclerView.adapter = documentListAdapter

        viewModel.notifyItemDocument.observe(viewLifecycleOwner, Observer {
            fragmentVaultBinding.imageRecyclerView.recycledViewPool.clear()
            documentListAdapter.notifyDataSetChanged()
            checkForDeletePannel()
        })

        viewModel.getDownloadedDocumentList()

    }

}

private fun TextView.activeText() {
    this.setTextColor(fetchThemeColor(R.attr.active_text_color, this.context))
}

private fun TextView.inActiveText() {
    this.setTextColor(fetchThemeColor(R.attr.inactive_text_color, this.context))
}
