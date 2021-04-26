package com.pyra.krpytapplication.view.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.*
import com.pyra.krpytapplication.databinding.*
import com.pyra.krpytapplication.roomDb.entity.ChatMessagesSchema
import com.pyra.krpytapplication.view.activity.ImageAndVideoViewer
import com.pyra.krpytapplication.viewmodel.ChatMessagesViewModel
import getDocumentIcon
import getViewIntent
import java.io.File


public enum class MessageViewTypes(val value: Int) {
    SENDER_TEXT(1),
    SENDER_IMAGE(2),
    SENDER_AUDIO(3),
    SENDER_VIDEO(4),
    SENDER_DOCUMENT(5),
    SENDER_CONTACT(6),
    SENDER_LOCATION(7),
    RECEIVER_TEXT(8),
    RECEIVER_IMAGE(9),
    RECEIVER_AUDIO(10),
    RECEIVER_VIDEO(11),
    RECEIVER_DOCUMENT(12),
    RECEIVER_CONTACT(13),
    RECEIVER_LOCATION(14),
    REPLY(15),
    NONE(16),
    MISSEDCALL(17)
}

class ChatMessageAdapter(
    private val context: Context, val isSaved: Boolean, val viewmodel: ChatMessagesViewModel
) : PagedListAdapter<ChatMessagesSchema, RecyclerView.ViewHolder>(DIFF_CALLBACK) {


//    private val adapterCallback = AdapterListUpdateCallback(this)

//    private var differ = AsyncPagedListDiffer(object : ListUpdateCallback {
//        override fun onChanged(position: Int, count: Int, payload: Any?) {
//            adapterCallback.onChanged(position , count, payload)
//        }
//
//        override fun onMoved(fromPosition: Int, toPosition: Int) {
//            adapterCallback.onMoved(fromPosition , toPosition )
//        }
//
//        override fun onInserted(position: Int, count: Int) {
//            adapterCallback.onInserted(0 , count)
//        }
//
//        override fun onRemoved(position: Int, count: Int) {
//            adapterCallback.onRemoved(position , count)
//        }
//
//    }, AsyncDifferConfig.Builder<ChatMessagesSchema>(DIFF_CALLBACK).build())


//    override fun getItemCount(): Int {
//        return differ.itemCount
//    }
//
//    override fun getCurrentList(): PagedList<ChatMessagesSchema>? {
//        return differ.currentList
//    }

//    override fun submitList(pagedList: PagedList<ChatMessagesSchema>?) {
//        submitList(pagedList)
//        notifyDataSetChanged()
//    }


    override fun getItemViewType(position: Int): Int {
        when (viewmodel.getChatMessageType(position)) {

            MessageType.TEXT -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.SENDER_TEXT.value
                    } else {
                        MessageViewTypes.RECEIVER_TEXT.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }

            MessageType.IMAGE -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.SENDER_IMAGE.value
                    } else {
                        MessageViewTypes.RECEIVER_IMAGE.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }
            MessageType.AUDIO -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.SENDER_AUDIO.value
                    } else {
                        MessageViewTypes.RECEIVER_AUDIO.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }
            MessageType.VIDEO -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.SENDER_VIDEO.value
                    } else {
                        MessageViewTypes.RECEIVER_VIDEO.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }
            MessageType.DOCUMENT -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.SENDER_DOCUMENT.value
                    } else {
                        MessageViewTypes.RECEIVER_DOCUMENT.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }
            MessageType.CONTACT -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.SENDER_CONTACT.value
                    } else {
                        MessageViewTypes.RECEIVER_CONTACT.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }
            MessageType.MISSEDCALL -> {
                return if (viewmodel.isSender(position) != null) {
                    if (viewmodel.isSender(position)!!) {
                        MessageViewTypes.MISSEDCALL.value
                    } else {
                        MessageViewTypes.MISSEDCALL.value
                    }

                } else {
                    MessageViewTypes.NONE.value
                }
            }

        }
        return MessageViewTypes.NONE.value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        when (viewType) {
            MessageViewTypes.SENDER_TEXT.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_text,
                    parent,
                    false
                )
                return viewHolderSenderText(binding as ItemSenderTextBinding)
            }
            MessageViewTypes.SENDER_IMAGE.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_image,
                    parent,
                    false
                )
                return viewHolderSenderImage(binding as ItemSenderImageBinding)
            }
            MessageViewTypes.SENDER_AUDIO.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_audio,
                    parent,
                    false
                )
                return viewHolderSenderAudio(binding as ItemSenderAudioBinding)
            }
            MessageViewTypes.SENDER_VIDEO.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_video,
                    parent,
                    false
                )
                return viewHolderSenderVideo(binding as ItemSenderVideoBinding)
            }
            MessageViewTypes.SENDER_DOCUMENT.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_document,
                    parent,
                    false
                )
                return viewHolderSenderDocument(binding as ItemSenderDocumentBinding)
            }
            MessageViewTypes.SENDER_CONTACT.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_document,
                    parent,
                    false
                )
                return viewHolderSenderDocument(binding as ItemSenderDocumentBinding)
            }
            MessageViewTypes.SENDER_LOCATION.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_sender_document,
                    parent,
                    false
                )
                return viewHolderSenderDocument(binding as ItemSenderDocumentBinding)
            }
            MessageViewTypes.RECEIVER_TEXT.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_text,
                    parent,
                    false
                )
                return viewHolderReceiverText(binding as ItemReceiverTextBinding)
            }
            MessageViewTypes.RECEIVER_IMAGE.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_image,
                    parent,
                    false
                )
                return viewHolderReceiverImage(binding as ItemReceiverImageBinding)
            }
            MessageViewTypes.RECEIVER_AUDIO.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_audio,
                    parent,
                    false
                )
                return viewHolderReceiverAudio(binding as ItemReceiverAudioBinding)
            }
            MessageViewTypes.RECEIVER_VIDEO.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_video,
                    parent,
                    false
                )
                return viewHolderReceiverVideo(binding as ItemReceiverVideoBinding)
            }
            MessageViewTypes.RECEIVER_DOCUMENT.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_document,
                    parent,
                    false
                )
                return viewHolderReceiverDocument(binding as ItemReceiverDocumentBinding)
            }
            MessageViewTypes.RECEIVER_CONTACT.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_document,
                    parent,
                    false
                )
                return viewHolderReceiverDocument(binding as ItemReceiverDocumentBinding)
            }
            MessageViewTypes.RECEIVER_LOCATION.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_document,
                    parent,
                    false
                )
                return viewHolderReceiverDocument(binding as ItemReceiverDocumentBinding)
            }

            MessageViewTypes.MISSEDCALL.value -> {
                val binding = DataBindingUtil.inflate<ViewDataBinding>(
                    layoutInflater,
                    R.layout.item_receiver_missedcall,
                    parent,
                    false
                )
                return MissedCallViewHolder(binding as ItemReceiverMissedcallBinding)
            }


        }

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            R.layout.item_sender_text,
            parent,
            false
        )
        return viewHolderSenderText(binding as ItemSenderTextBinding)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        holder.itemView.findViewById<TextView>(R.id.messageTime)
            .setTextColor(context.getResources().getColor(R.color.white))
        holder.itemView.findViewById<TextView>(R.id.dateSectionHeader)
            .setTextColor(context.getResources().getColor(R.color.white))

        viewmodel.isSender(position)?.let {
            if (it) {
                if (!SharedHelper(context).chatBubbleColor.equals("", ignoreCase = true)) {
                    holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).background.setColorFilter(
                        Color.parseColor(SharedHelper(context).chatBubbleColor),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    holder.itemView.findViewById<LinearLayout>(R.id.chatBubbleColor).background.setColorFilter(
                        Color.parseColor(SharedHelper(context).chatBubbleColor),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            } else {
                if (!SharedHelper(context).chatBubbleColorReciver.equals("", ignoreCase = true)) {
                    holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).background.setColorFilter(
                        Color.parseColor(SharedHelper(context).chatBubbleColorReciver),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    holder.itemView.findViewById<LinearLayout>(R.id.chatBubbleColor).background.setColorFilter(
                        Color.parseColor(SharedHelper(context).chatBubbleColorReciver),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            }

        }

        holder.itemView.rootView.setOnLongClickListener {
            viewmodel.selectededPosition(position)
            return@setOnLongClickListener true
        }

        if (isSaved) {
            holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).visibility =
                View.GONE
        } else {
            viewmodel.getDateDisplay(position, itemCount).let { value ->

                viewmodel.isSender(position)?.let {
                    if (it) {
                        if (!SharedHelper(context).chatBubbleColor.equals("", ignoreCase = true)) {
                            holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).background.setColorFilter(
                                Color.parseColor(SharedHelper(context).chatBubbleColor),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                    } else {
                        if (!SharedHelper(context).chatBubbleColorReciver.equals(
                                "",
                                ignoreCase = true
                            )
                        ) {
                            holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).background.setColorFilter(
                                Color.parseColor(SharedHelper(context).chatBubbleColorReciver),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                    }
                }


                if (value == "") {
                    holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).visibility =
                        View.GONE
                } else {
                    holder.itemView.findViewById<TextView>(R.id.dateSectionHeader).also {
                        it.visibility = View.VISIBLE
                        it.text = value
                    }
                }
            }
        }



        when (getItemViewType(position)) {

            MessageViewTypes.MISSEDCALL.value -> {

                val missedCallViewHolder = holder as MissedCallViewHolder

                missedCallViewHolder.binding.messageTime.text =
                    viewmodel.getChatMessageTime(position)
//                if(!SharedHelper(context).chatBubbleColor.equals("",ignoreCase = false)) {
//                    missedCallViewHolder.binding.messageTime.setTextColor(context.getResources().getColor(R.color.white))
//                    missedCallViewHolder.binding.chatBubbleColor.background.setColorFilter(Color.parseColor(SharedHelper(context).chatBubbleColor), PorterDuff.Mode.SRC_ATOP)
//                }
            }


            MessageViewTypes.SENDER_TEXT.value -> {
                val senderText: viewHolderSenderText = holder as viewHolderSenderText
                if (isSaved) {

                    senderText.binding.headerText.show()
                    senderText.binding.layoutReply.hide()
                    (senderText.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (senderText.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (senderText.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    senderText.binding.headerText.hide()

                    if (viewmodel.getIsReply(position)) {


                        senderText.binding.layoutReply.show()

                        (senderText.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyName)
                            .text = viewmodel.getReplySendName(position)

                        (senderText.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyMessage)
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (senderText.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (senderText.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (senderText.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        senderText.binding.layoutReply.hide()
                    }

                }
                senderText.binding.messageTime.text = viewmodel.getChatMessageTime(position)
                senderText.binding.messageContent.text = viewmodel.getTextChatMessage(position)

                if (viewmodel.isSelected(position)) {
                    senderText.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    senderText.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                when (viewmodel.getChatMessageStatus(position)) {
                    MessageStatus.SENDING -> senderText.binding.messageStatus.setImageResource(R.drawable.ic_not_send)
                    MessageStatus.SENT -> senderText.binding.messageStatus.setImageResource(R.drawable.ic_single_tick)
                    MessageStatus.DELIVERED -> senderText.binding.messageStatus.setImageResource(
                        R.drawable.ic_seen_icon
                    )
                    MessageStatus.READ -> senderText.binding.messageStatus.setImageResource(R.drawable.ic_blue_tick)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    }
                }
            }
            MessageViewTypes.SENDER_IMAGE.value -> {
                val senderImage: viewHolderSenderImage = holder as viewHolderSenderImage
                if (isSaved) {
                    senderImage.binding.headerText.show()
                    senderImage.binding.layoutReply.hide()
                    (senderImage.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (senderImage.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (senderImage.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    senderImage.binding.headerText.hide()

                    if (viewmodel.getIsReply(position)) {


                        senderImage.binding.layoutReply.show()

                        (senderImage.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyName)
                            .text = viewmodel.getReplySendName(position)

                        (senderImage.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyMessage)
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (senderImage.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (senderImage.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (senderImage.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        senderImage.binding.layoutReply.hide()
                    }


                }
                senderImage.binding.messageTime.text = viewmodel.getChatMessageTime(position)

                if (viewmodel.isSelected(position)) {
                    senderImage.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    senderImage.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                senderImage.binding.messageContent.loadChatImage(
                    viewmodel.getSenderImage(position),
                    viewmodel.getThumbNail(position)
                )

                if (viewmodel.isUploaded(position)) {

                    setUploaded(
                        senderImage.binding.uploadingLayout.uploadLayout,
                        senderImage.binding.uploadLayout.uploadLayout
                    )

                } else {

                    if (viewmodel.isUploadCancelledByUser(position)) {

                        setUploadPending(
                            senderImage.binding.uploadingLayout.uploadLayout,
                            senderImage.binding.uploadLayout.uploadLayout
                        )

                    } else {

                        setUploading(
                            senderImage.binding.uploadingLayout.uploadLayout,
                            senderImage.binding.uploadLayout.uploadLayout
                        )
                    }


                }


                senderImage.binding.uploadLayout.uploadLayout.setOnClickListener {
                    viewmodel.startUpload(position)
                }

                senderImage.binding.uploadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.uploadCancelledByUser(position)
                }

                when (viewmodel.getChatMessageStatus(position)) {
                    MessageStatus.SENDING -> senderImage.binding.messageStatus.setImageResource(
                        R.drawable.ic_not_send
                    )
                    MessageStatus.SENT -> senderImage.binding.messageStatus.setImageResource(R.drawable.ic_single_tick)
                    MessageStatus.DELIVERED -> senderImage.binding.messageStatus.setImageResource(
                        R.drawable.ic_seen_icon
                    )
                    MessageStatus.READ -> senderImage.binding.messageStatus.setImageResource(R.drawable.ic_blue_tick)
                }


                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    } else {
                        var intent = Intent(context, ImageAndVideoViewer::class.java)
                        intent.putExtra(
                            Constants.IntentKeys.CONTENT, viewmodel.getLocalFilePath(position)
                        )
                        intent.putExtra(Constants.IntentKeys.ISVIDEO, false)
                        context.startActivity(intent)
                    }

                }
            }

            MessageViewTypes.SENDER_AUDIO.value -> {
                val senderAudio: viewHolderSenderAudio = holder as viewHolderSenderAudio

                if (isSaved) {
                    senderAudio.binding.headerText.show()
                    senderAudio.binding.layoutReply.hide()
                    (senderAudio.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (senderAudio.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (senderAudio.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    senderAudio.binding.headerText.hide()


                    if (viewmodel.getIsReply(position)) {


                        senderAudio.binding.layoutReply.show()

                        (senderAudio.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyName)
                            .text = viewmodel.getReplySendName(position)

                        (senderAudio.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyMessage)
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (senderAudio.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (senderAudio.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (senderAudio.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        senderAudio.binding.layoutReply.hide()
                    }


                }
                senderAudio.binding.messageTime.text = viewmodel.getChatMessageTime(position)

                if (viewmodel.isSelected(position)) {
                    senderAudio.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    senderAudio.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                senderAudio.binding.messageContent.text = viewmodel.getDocumentName(position)

                if (viewmodel.isUploaded(position)) {
                    if (viewmodel.getIsAudioPlaying(position)) {
                        setAudioUploadedPlaying(
                            senderAudio.binding.uploadingLayout.uploadLayout,
                            senderAudio.binding.uploadLayout.uploadLayout,
                            senderAudio.binding.uploadedLayout.playLayout,
                            senderAudio.binding.pause
                        )
                    } else {

                        setAudioUploaded(
                            senderAudio.binding.uploadingLayout.uploadLayout,
                            senderAudio.binding.uploadLayout.uploadLayout,
                            senderAudio.binding.uploadedLayout.playLayout,
                            senderAudio.binding.pause
                        )


                    }
                } else {
                    if (viewmodel.isUploadCancelledByUser(position)) {
                        setAudioUpload(
                            senderAudio.binding.uploadingLayout.uploadLayout,
                            senderAudio.binding.uploadLayout.uploadLayout,
                            senderAudio.binding.uploadedLayout.playLayout,
                            senderAudio.binding.pause
                        )
                    } else {
                        setAudioUploading(
                            senderAudio.binding.uploadingLayout.uploadLayout,
                            senderAudio.binding.uploadLayout.uploadLayout,
                            senderAudio.binding.uploadedLayout.playLayout,
                            senderAudio.binding.pause
                        )
                    }
                }

                senderAudio.binding.pause.setOnClickListener {
                    viewmodel.pauseAudio()
                }

                senderAudio.binding.uploadedLayout.playLayout.setOnClickListener {
                    viewmodel.playAudio(position)
                }

                senderAudio.binding.uploadLayout.uploadLayout.setOnClickListener {
                    viewmodel.startUploadAudio(position)
                }

                senderAudio.binding.uploadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.uploadCancelledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    }
                }

                when (viewmodel.getChatMessageStatus(position)) {
                    MessageStatus.SENDING -> senderAudio.binding.messageStatus.setImageResource(
                        R.drawable.ic_not_send
                    )
                    MessageStatus.SENT -> senderAudio.binding.messageStatus.setImageResource(R.drawable.ic_single_tick)
                    MessageStatus.DELIVERED -> senderAudio.binding.messageStatus.setImageResource(
                        R.drawable.ic_seen_icon
                    )
                    MessageStatus.READ -> senderAudio.binding.messageStatus.setImageResource(R.drawable.ic_blue_tick)
                }


            }
            MessageViewTypes.SENDER_VIDEO.value -> {
                val senderVideo: viewHolderSenderVideo = holder as viewHolderSenderVideo

                if (isSaved) {
                    senderVideo.binding.headerText.show()
                    senderVideo.binding.layoutReply.hide()
                    (senderVideo.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (senderVideo.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)
                    (senderVideo.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    senderVideo.binding.headerText.hide()


                    if (viewmodel.getIsReply(position)) {


                        senderVideo.binding.layoutReply.show()

                        (senderVideo.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyName)
                            .text = viewmodel.getReplySendName(position)

                        (senderVideo.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyMessage)
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (senderVideo.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (senderVideo.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (senderVideo.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        senderVideo.binding.layoutReply.hide()
                    }

                }
                senderVideo.binding.messageTime.text = viewmodel.getChatMessageTime(position)

                if (viewmodel.isSelected(position)) {
                    senderVideo.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    senderVideo.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                senderVideo.binding.messageContent.loadChatImage(
                    viewmodel.getSenderVideo(position),
                    viewmodel.getThumbNail(position)
                )


                if (viewmodel.isUploaded(position)) {
                    setVideoUploded(
                        senderVideo.binding.uploadingLayout.uploadLayout,
                        senderVideo.binding.uploadLayout.uploadLayout,
                        senderVideo.binding.uploadedLayout.playLayout
                    )
                } else {
                    if (viewmodel.isUploadCancelledByUser(position)) {
                        setVideoUplodedPending(
                            senderVideo.binding.uploadingLayout.uploadLayout,
                            senderVideo.binding.uploadLayout.uploadLayout,
                            senderVideo.binding.uploadedLayout.playLayout
                        )

                    } else {
                        setVideoUploding(
                            senderVideo.binding.uploadingLayout.uploadLayout,
                            senderVideo.binding.uploadLayout.uploadLayout,
                            senderVideo.binding.uploadedLayout.playLayout
                        )
                    }
                }

                senderVideo.binding.uploadLayout.uploadLayout.setOnClickListener {
                    viewmodel.startUpload(position)
                }

                senderVideo.binding.uploadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.uploadCancelledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    } else {
                        var intent = Intent(context, ImageAndVideoViewer::class.java)
                        intent.putExtra(
                            Constants.IntentKeys.CONTENT,
                            viewmodel.getLocalFilePath(position)
                        )
                        intent.putExtra(Constants.IntentKeys.ISVIDEO, true)
                        context.startActivity(intent)
                    }

                }


                when (viewmodel.getChatMessageStatus(position)) {
                    MessageStatus.SENDING -> senderVideo.binding.messageStatus.setImageResource(
                        R.drawable.ic_not_send
                    )
                    MessageStatus.SENT -> senderVideo.binding.messageStatus.setImageResource(R.drawable.ic_single_tick)
                    MessageStatus.DELIVERED -> senderVideo.binding.messageStatus.setImageResource(
                        R.drawable.ic_seen_icon
                    )
                    MessageStatus.READ -> senderVideo.binding.messageStatus.setImageResource(R.drawable.ic_blue_tick)
                }

            }
            MessageViewTypes.SENDER_DOCUMENT.value -> {
                val senderDocument: viewHolderSenderDocument =
                    holder as viewHolderSenderDocument

                if (isSaved) {
                    senderDocument.binding.headerText.show()
                    senderDocument.binding.layoutReply.hide()
                    (senderDocument.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (senderDocument.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (senderDocument.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)
                } else {
                    senderDocument.binding.headerText.hide()


                    if (viewmodel.getIsReply(position)) {


                        senderDocument.binding.layoutReply.show()

                        (senderDocument.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyName
                        )
                            .text = viewmodel.getReplySendName(position)

                        (senderDocument.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyMessage
                        )
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (senderDocument.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (senderDocument.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (senderDocument.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        senderDocument.binding.layoutReply.hide()
                    }

                }
                senderDocument.binding.messageTime.text = viewmodel.getChatMessageTime(position)

                if (viewmodel.isSelected(position)) {
                    senderDocument.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    senderDocument.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                senderDocument.binding.documentIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        getDocumentIcon(viewmodel.getSenderFile(position)?.getFileExtention())
                    )
                )

                senderDocument.binding.messageContent.text = viewmodel.getDocumentName(position)

                if (viewmodel.isUploaded(position)) {
                    setDocumentUploaded(
                        senderDocument.binding.uploadLayout.uploadLayout,
                        senderDocument.binding.uploadingLayout.uploadLayout
                    )
                } else {
                    if (viewmodel.isUploadCancelledByUser(position)) {
                        setDocumentUpload(
                            senderDocument.binding.uploadLayout.uploadLayout,
                            senderDocument.binding.uploadingLayout.uploadLayout
                        )
                    } else {
                        setDocumentUploading(
                            senderDocument.binding.uploadLayout.uploadLayout,
                            senderDocument.binding.uploadingLayout.uploadLayout
                        )
                    }
                }

                senderDocument.binding.uploadLayout.uploadLayout.setOnClickListener {
                    viewmodel.startUploadDocument(position)
                }

                senderDocument.binding.uploadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.uploadCancelledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    } else {
                        openDocument(viewmodel.getLocalFile(position), position)
                    }

                }

                when (viewmodel.getChatMessageStatus(position)) {
                    MessageStatus.SENDING -> senderDocument.binding.messageStatus.setImageResource(
                        R.drawable.ic_not_send
                    )
                    MessageStatus.SENT -> senderDocument.binding.messageStatus.setImageResource(
                        R.drawable.ic_single_tick
                    )
                    MessageStatus.DELIVERED -> senderDocument.binding.messageStatus.setImageResource(
                        R.drawable.ic_seen_icon
                    )
                    MessageStatus.READ -> senderDocument.binding.messageStatus.setImageResource(
                        R.drawable.ic_blue_tick
                    )
                }

            }
            MessageViewTypes.SENDER_CONTACT.value -> {

            }
            MessageViewTypes.SENDER_LOCATION.value -> {

            }
            MessageViewTypes.RECEIVER_TEXT.value -> {
                val receiverText: viewHolderReceiverText = holder as viewHolderReceiverText
                if (isSaved) {

                    receiverText.binding.layoutReply.hide()
                    receiverText.binding.headerText.show()

                    (receiverText.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (receiverText.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (receiverText.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)


                } else {
                    receiverText.binding.headerText.hide()
                    if (viewmodel.getIsGroupChat()) {
                        receiverText.binding.userImage.show()
                        receiverText.binding.messageUserName.show()
                        receiverText.binding.messageUserName.text =
                            viewmodel.getSenderName(position)
                        receiverText.binding.userImage.loadImage(
                            viewmodel.getSenderProfilePic(
                                position
                            )
                        )
                    } else {
                        receiverText.binding.userImage.hide()
                        receiverText.binding.messageUserName.hide()
                    }

                    if (viewmodel.getIsReply(position)) {


                        receiverText.binding.layoutReply.show()

                        (receiverText.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyName)
                            .text = viewmodel.getReplySendName(position)

                        (receiverText.binding.layoutReply as ViewGroup).findViewById<TextView>(R.id.replyMessage)
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (receiverText.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            ).show()
                            (receiverText.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            ).loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (receiverText.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            ).hide()
                        }
                    } else {
                        receiverText.binding.layoutReply.hide()

                    }

                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    }
                }

                receiverText.binding.messageStatus.visibility = View.GONE

                receiverText.binding.messageTime.text = viewmodel.getChatMessageTime(position)
                receiverText.binding.messageContent.text =
                    viewmodel.getTextChatMessage(position)

                if (viewmodel.isSelected(position)) {
                    receiverText.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    receiverText.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }
            }
            MessageViewTypes.RECEIVER_IMAGE.value -> {
                val receiverImage: viewHolderReceiverImage = holder as viewHolderReceiverImage

                if (isSaved) {
                    receiverImage.binding.headerText.show()
                    receiverImage.binding.userImage.hide()
                    receiverImage.binding.messageUserName.hide()
                    receiverImage.binding.layoutReply.hide()
                    (receiverImage.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (receiverImage.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (receiverImage.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    receiverImage.binding.headerText.hide()

                    if (viewmodel.getIsGroupChat()) {
                        receiverImage.binding.userImage.show()
                        receiverImage.binding.messageUserName.show()
                    } else {
                        receiverImage.binding.userImage.hide()
                        receiverImage.binding.messageUserName.hide()
                    }


                    if (viewmodel.getIsReply(position)) {


                        receiverImage.binding.layoutReply.show()

                        (receiverImage.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyName
                        )
                            .text = viewmodel.getReplySendName(position)

                        (receiverImage.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyMessage
                        )
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (receiverImage.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (receiverImage.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (receiverImage.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        receiverImage.binding.layoutReply.hide()
                    }


                }

                receiverImage.binding.messageUserName.hide()
//                receiverImage.binding.messageUserName.text = viewmodel.getSenderName(position)
                receiverImage.binding.messageTime.text = viewmodel.getChatMessageTime(position)

                if (viewmodel.isSelected(position)) {
                    receiverImage.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    receiverImage.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                receiverImage.binding.messageContent.loadChatImage(
                    viewmodel.getReciverImageUrl(position),
                    viewmodel.getReciverThumbUrl(position)
                )

                if (viewmodel.isMediaDownload(position)) {
                    setDownloded(
                        receiverImage.binding.downloadingLayout.uploadLayout,
                        receiverImage.binding.downloadLayout.downloadLayout
                    )
                } else {
                    if (viewmodel.isDownloadCancelByUser(position)) {
                        setDownloadPending(
                            receiverImage.binding.downloadingLayout.uploadLayout,
                            receiverImage.binding.downloadLayout.downloadLayout
                        )
                    } else {
                        setDownloading(
                            receiverImage.binding.downloadingLayout.uploadLayout,
                            receiverImage.binding.downloadLayout.downloadLayout
                        )
                    }

                }


                receiverImage.binding.downloadLayout.downloadLayout.setOnClickListener {
                    viewmodel.downloadMedia(position)
                }

                receiverImage.binding.downloadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.downloadCanceledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {

                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    } else {
                        if (viewmodel.isFileExist(position)) {
                            var intent = Intent(context, ImageAndVideoViewer::class.java)
                            intent.putExtra(
                                Constants.IntentKeys.CONTENT,
                                viewmodel.getLocalFilePath(position)
                            )
                            intent.putExtra(Constants.IntentKeys.ISVIDEO, false)
                            context.startActivity(intent)
                        }
                    }

                }
            }
            MessageViewTypes.RECEIVER_AUDIO.value -> {

                val receiverAudio: viewHolderReceiverAudio = holder as viewHolderReceiverAudio

                if (isSaved) {
                    receiverAudio.binding.headerText.show()
                    receiverAudio.binding.userImage.hide()
                    receiverAudio.binding.messageUserName.hide()
                    receiverAudio.binding.layoutReply.hide()
                    (receiverAudio.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (receiverAudio.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (receiverAudio.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    receiverAudio.binding.headerText.hide()
                    if (viewmodel.getIsGroupChat()) {
                        receiverAudio.binding.userImage.show()
                        receiverAudio.binding.messageUserName.show()
                    } else {
                        receiverAudio.binding.userImage.hide()
                        receiverAudio.binding.messageUserName.hide()
                    }


                    if (viewmodel.getIsReply(position)) {


                        receiverAudio.binding.layoutReply.show()

                        (receiverAudio.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyName
                        )
                            .text = viewmodel.getReplySendName(position)

                        (receiverAudio.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyMessage
                        )
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (receiverAudio.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (receiverAudio.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (receiverAudio.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        receiverAudio.binding.layoutReply.hide()
                    }


                }
                receiverAudio.binding.messageTime.text = viewmodel.getChatMessageTime(position)

                if (viewmodel.isSelected(position)) {
                    receiverAudio.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    receiverAudio.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                receiverAudio.binding.messageContent.text = viewmodel.getDocumentName(position)



                if (viewmodel.isMediaDownload(position)) {

                    if (viewmodel.getIsAudioPlaying(position)) {

                        setDownlodedAudioPlaying(
                            receiverAudio.binding.downloadingLayout.uploadLayout,
                            receiverAudio.binding.downloadLayout.downloadLayout,
                            receiverAudio.binding.downloadedLayout.playLayout,
                            receiverAudio.binding.pause
                        )

                    } else {
                        setDownlodedAudio(
                            receiverAudio.binding.downloadingLayout.uploadLayout,
                            receiverAudio.binding.downloadLayout.downloadLayout,
                            receiverAudio.binding.downloadedLayout.playLayout,
                            receiverAudio.binding.pause
                        )
                    }

                } else {

                    if (viewmodel.isDownloadCancelByUser(position)) {
                        setDownlodAudio(
                            receiverAudio.binding.downloadingLayout.uploadLayout,
                            receiverAudio.binding.downloadLayout.downloadLayout,
                            receiverAudio.binding.downloadedLayout.playLayout,
                            receiverAudio.binding.pause
                        )
                    } else {
                        setDownlodingAudio(
                            receiverAudio.binding.downloadingLayout.uploadLayout,
                            receiverAudio.binding.downloadLayout.downloadLayout,
                            receiverAudio.binding.downloadedLayout.playLayout,
                            receiverAudio.binding.pause
                        )
                    }
                }

                receiverAudio.binding.pause.setOnClickListener {
                    viewmodel.pauseAudio()
                }

                receiverAudio.binding.downloadedLayout.playLayout.setOnClickListener {
                    viewmodel.playAudio(position)
                }

                receiverAudio.binding.downloadLayout.downloadLayout.setOnClickListener {
                    viewmodel.downloadAudio(position)
                }

                receiverAudio.binding.downloadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.downloadCanceledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    }
                }
            }
            MessageViewTypes.RECEIVER_VIDEO.value -> {
                val receiverVideo: viewHolderReceiverVideo = holder as viewHolderReceiverVideo

                if (isSaved) {
                    receiverVideo.binding.headerText.show()
                    receiverVideo.binding.userImage.hide()
                    receiverVideo.binding.messageUserName.hide()
                    receiverVideo.binding.layoutReply.hide()
                    (receiverVideo.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (receiverVideo.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (receiverVideo.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)
                } else {
                    receiverVideo.binding.headerText.hide()
                    if (viewmodel.getIsGroupChat()) {
                        receiverVideo.binding.userImage.show()
                        receiverVideo.binding.messageUserName.show()
                    } else {
                        receiverVideo.binding.userImage.hide()
                        receiverVideo.binding.messageUserName.hide()
                    }


                    if (viewmodel.getIsReply(position)) {


                        receiverVideo.binding.layoutReply.show()

                        (receiverVideo.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyName
                        )
                            .text = viewmodel.getReplySendName(position)

                        (receiverVideo.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyMessage
                        )
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (receiverVideo.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (receiverVideo.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (receiverVideo.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        receiverVideo.binding.layoutReply.hide()
                    }

                }

                receiverVideo.binding.messageUserName.hide()
                receiverVideo.binding.messageTime.text = viewmodel.getChatMessageTime(position)


                if (viewmodel.isSelected(position)) {
                    receiverVideo.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    receiverVideo.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }


                if (viewmodel.isMediaDownload(position)) {
                    setVideoDownloded(
                        receiverVideo.binding.downloadingLayout.uploadLayout,
                        receiverVideo.binding.downloadLayout.downloadLayout,
                        receiverVideo.binding.downloadedLayout.playLayout
                    )
                } else {

                    if (viewmodel.isDownloadCancelByUser(position)) {
                        setDownloadVideo(
                            receiverVideo.binding.downloadingLayout.uploadLayout,
                            receiverVideo.binding.downloadLayout.downloadLayout,
                            receiverVideo.binding.downloadedLayout.playLayout
                        )
                    } else {
                        setDownloadingVideo(
                            receiverVideo.binding.downloadingLayout.uploadLayout,
                            receiverVideo.binding.downloadLayout.downloadLayout,
                            receiverVideo.binding.downloadedLayout.playLayout
                        )
                    }

                }
                receiverVideo.binding.messageContent.loadChatImage(
                    viewmodel.getReciverVideo(position),
                    viewmodel.getThumbNail(position)
                )


                receiverVideo.binding.downloadLayout.downloadLayout.setOnClickListener {
                    viewmodel.downloadMedia(position)
                }

                receiverVideo.binding.downloadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.downloadCanceledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    } else {
                        if (viewmodel.isFileExist(position)) {
                            val intent = Intent(context, ImageAndVideoViewer::class.java)
                            intent.putExtra(
                                Constants.IntentKeys.CONTENT,
                                viewmodel.getLocalFilePath(position)
                            )
                            intent.putExtra(Constants.IntentKeys.ISVIDEO, true)
                            context.startActivity(intent)
                        }
                    }
                }
            }

            MessageViewTypes.RECEIVER_DOCUMENT.value -> {
                val receiverDocument: viewHolderReceiverDocument =
                    holder as viewHolderReceiverDocument

                if (isSaved) {
                    receiverDocument.binding.headerText.show()
                    receiverDocument.binding.userImage.hide()
                    receiverDocument.binding.messageUserName.hide()
                    receiverDocument.binding.layoutReply.hide()

                    (receiverDocument.binding.headerText as ViewGroup).findViewById<TextView>(R.id.senderName)
                        .text = viewmodel.getSenderName(position)
                    (receiverDocument.binding.headerText as ViewGroup).findViewById<TextView>(R.id.receiverName)
                        .text = viewmodel.getReciverName(position)

                    (receiverDocument.binding.headerText as ViewGroup).findViewById<TextView>(R.id.messageDate)
                        .text = viewmodel.getChatMessageTime(position)

                } else {
                    receiverDocument.binding.headerText.hide()
                    if (viewmodel.getIsGroupChat()) {
                        receiverDocument.binding.userImage.show()
                        receiverDocument.binding.messageUserName.show()
                    } else {
                        receiverDocument.binding.userImage.hide()
                        receiverDocument.binding.messageUserName.hide()
                    }


                    if (viewmodel.getIsReply(position)) {


                        receiverDocument.binding.layoutReply.show()

                        (receiverDocument.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyName
                        )
                            .text = viewmodel.getReplySendName(position)

                        (receiverDocument.binding.layoutReply as ViewGroup).findViewById<TextView>(
                            R.id.replyMessage
                        )
                            .text = viewmodel.getReplyMessage(position)

                        if (viewmodel.isImageOrVideoMessage(position)) {
                            (receiverDocument.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .show()
                            (receiverDocument.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .loadImage(viewmodel.getReplyUrl(position))
                        } else {
                            (receiverDocument.binding.layoutReply as ViewGroup).findViewById<ImageView>(
                                R.id.replyImage
                            )
                                .hide()
                        }
                    } else {
                        receiverDocument.binding.layoutReply.hide()
                    }


                }
                receiverDocument.binding.messageTime.text =
                    viewmodel.getChatMessageTime(position)

                receiverDocument.binding.documentIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        getDocumentIcon(viewmodel.getReciverFile(position)?.getFileExtention())
                    )
                )

                receiverDocument.binding.messageUserName.hide()

                if (viewmodel.isSelected(position)) {
                    receiverDocument.binding.rootLayout.setBackgroundResource(R.color.chat_selected)
                } else {
                    receiverDocument.binding.rootLayout.setBackgroundResource(R.color.chat_unselected)
                }

                receiverDocument.binding.messageContent.text =
                    viewmodel.getDocumentName(position)

                if (viewmodel.isMediaDownload(position)) {
                    setDownloded(
                        receiverDocument.binding.downloadingLayout.uploadLayout,
                        receiverDocument.binding.downloadLayout.downloadLayout
                    )
                } else {
                    if (viewmodel.isDownloadCancelByUser(position)) {
                        setDownloadPending(
                            receiverDocument.binding.downloadingLayout.uploadLayout,
                            receiverDocument.binding.downloadLayout.downloadLayout
                        )
                    } else {
                        setDownloading(
                            receiverDocument.binding.downloadingLayout.uploadLayout,
                            receiverDocument.binding.downloadLayout.downloadLayout
                        )
                    }
                }

                receiverDocument.binding.downloadLayout.downloadLayout.setOnClickListener {
                    viewmodel.downloadDocument(position)
                }

                receiverDocument.binding.downloadingLayout.uploadLayout.setOnClickListener {
                    viewmodel.downloadCanceledByUser(position)
                }

                holder.itemView.rootView.setOnClickListener {
                    if (viewmodel.isMultiSelectedEnabled) {
                        viewmodel.singleClick(position)
                    } else {
                        if (viewmodel.isFileExist(position))
                            openDocument(viewmodel.getLocalFile(position), position)
                    }

                }
            }
            MessageViewTypes.RECEIVER_CONTACT.value -> {

            }
            MessageViewTypes.RECEIVER_LOCATION.value -> {

            }
        }


    }

    private fun setDownlodAudio(
        downloadingLayout: View,
        downloadLayout: View,
        downloadedLayout: View,
        pause: View
    ) {
        downloadingLayout.hide()
        downloadLayout.show()
        downloadedLayout.hide()
        pause.hide()
    }

    private fun setDownlodingAudio(
        downloadingLayout: View,
        downloadLayout: View,
        downloadedLayout: View,
        pause: View
    ) {
        downloadingLayout.show()
        downloadLayout.hide()
        downloadedLayout.hide()
        pause.hide()
    }

    private fun setDownlodedAudio(
        downloadingLayout: View,
        downloadLayout: View,
        downloadedLayout: View,
        pause: View
    ) {
        downloadingLayout.hide()
        downloadLayout.hide()
        pause.hide()
        downloadedLayout.show()
    }

    private fun setDownlodedAudioPlaying(
        downloadingLayout: View,
        downloadLayout: View,
        downloadedLayout: View,
        pause: View
    ) {
        downloadingLayout.hide()
        downloadLayout.hide()
        pause.show()
        downloadedLayout.hide()
    }

    private fun setAudioUploading(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View,
        pause: View
    ) {

        uploadingLayout.show()
        uploadLayout.hide()
        uploadedLayout.hide()
        pause.hide()

    }

    private fun setAudioUpload(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View,
        pause: View
    ) {

        uploadingLayout.hide()
        uploadLayout.show()
        uploadedLayout.hide()
        pause.hide()

    }

    private fun setAudioUploaded(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View,
        pause: View
    ) {

        uploadingLayout.hide()
        uploadLayout.hide()
        uploadedLayout.show()
        pause.hide()

    }

    private fun setAudioUploadedPlaying(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View,
        pause: View
    ) {

        uploadingLayout.hide()
        uploadLayout.hide()
        uploadedLayout.hide()
        pause.show()

    }


    private fun setDocumentUploaded(
        uploadLayout: View,
        uploadingLayout: View
    ) {
        uploadLayout.hide()
        uploadingLayout.hide()
    }

    private fun setDocumentUpload(
        uploadLayout: View,
        uploadingLayout: View
    ) {
        uploadLayout.show()
        uploadingLayout.hide()
    }

    private fun setDocumentUploading(
        uploadLayout: View,
        uploadingLayout: View
    ) {
        uploadLayout.hide()
        uploadingLayout.show()
    }

    private fun setDownloadVideo(
        downloadingLayout: View,
        downloadLayout: View,
        downloadedLayout: View
    ) {
        downloadLayout.show()
        downloadingLayout.hide()
        downloadedLayout.hide()

    }

    private fun setDownloadingVideo(
        downloadingLayout: View,
        downloadLayout: View,
        downloadedLayout: View
    ) {
        downloadLayout.hide()
        downloadingLayout.show()
        downloadedLayout.hide()
    }

    private fun setVideoUploded(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View
    ) {
        uploadingLayout.hide()
        uploadLayout.hide()
        uploadedLayout.show()

    }

    private fun setVideoDownloded(
        downlodingLayout: View,
        downloadLayout: View,
        downlodedLayout: View
    ) {
        downlodingLayout.hide()
        downloadLayout.hide()
        downlodedLayout.show()

    }

    private fun setVideoUplodedPending(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View
    ) {
        uploadingLayout.hide()
        uploadLayout.show()
        uploadedLayout.hide()

    }

    private fun setVideoUploding(
        uploadingLayout: View,
        uploadLayout: View,
        uploadedLayout: View
    ) {
        uploadingLayout.show()
        uploadLayout.hide()
        uploadedLayout.hide()

    }

    private fun setDownloded(
        downloadingLayout: View,
        downloadLayout: View
    ) {
        downloadingLayout.hide()
        downloadLayout.hide()
    }


    fun setUploading(upLoadingLayout: View, upLoadLayout: View) {
        upLoadingLayout.show()
        upLoadLayout.hide()

    }


    fun setUploadPending(upLoadingLayout: View, upLoadLayout: View) {
        upLoadingLayout.hide()
        upLoadLayout.show()
    }


    fun setUploaded(upLoadingLayout: View, upLoadLayout: View) {
        upLoadingLayout.hide()
        upLoadLayout.hide()
    }


    fun setDownloading(
        downLoadingLayout: View,
        downLoadLayout: View
    ) {
        downLoadingLayout.show()
        downLoadLayout.hide()


    }

    fun setDownloaded(
        downLoadingLayout: View, downLoadLayout: View, downLoaded: View?,
        isVideo: Boolean
    ) {
        downLoadingLayout.hide()
        downLoadLayout.hide()
        if (isVideo) {
            downLoaded!!.show()
        }
    }


    fun setDownloadPending(downLoadingLayout: View, downLoadLayout: View) {
        downLoadingLayout.hide()
        downLoadLayout.show()
    }

    fun notifySelection() {
        submitList(viewmodel.chatMessagePagedList)
        notifyDataSetChanged()
    }


    inner class viewHolderSenderText(itemView: ItemSenderTextBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSenderTextBinding

        init {
            binding = itemView

        }

    }

    inner class viewHolderSenderImage(itemView: ItemSenderImageBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSenderImageBinding

        init {
            binding = itemView
        }
    }


    inner class viewHolderSenderVideo(itemView: ItemSenderVideoBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSenderVideoBinding

        init {
            binding = itemView
        }
    }

    inner class viewHolderSenderDocument(itemView: ItemSenderDocumentBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSenderDocumentBinding

        init {
            binding = itemView
        }
    }

    inner class viewHolderSenderAudio(itemView: ItemSenderAudioBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSenderAudioBinding

        init {
            binding = itemView
        }
    }

    inner class viewHolderSenderDeleted(itemView: ItemSenderDeletedMessageBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemSenderDeletedMessageBinding

        init {
            binding = itemView
        }
    }

    inner class viewHolderReceiverDeleted(itemView: ItemReceiverDeletedMessageBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemReceiverDeletedMessageBinding

        init {
            binding = itemView
        }
    }


    inner class viewHolderReceiverText(itemView: ItemReceiverTextBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemReceiverTextBinding

        init {
            binding = itemView
        }

    }

    inner class viewHolderReceiverImage(itemView: ItemReceiverImageBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemReceiverImageBinding

        init {
            binding = itemView
        }
    }


    inner class viewHolderReceiverVideo(itemView: ItemReceiverVideoBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemReceiverVideoBinding

        init {
            binding = itemView
        }
    }

    inner class viewHolderReceiverAudio(itemView: ItemReceiverAudioBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemReceiverAudioBinding

        init {
            binding = itemView
        }
    }

    inner class viewHolderReceiverDocument(itemView: ItemReceiverDocumentBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemReceiverDocumentBinding

        init {
            binding = itemView
        }
    }


    class MissedCallViewHolder(itemView: ItemReceiverMissedcallBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        val binding = itemView as ItemReceiverMissedcallBinding

    }

    fun openDocument(file: File?, position: Int) {

        file?.getDocumentType()?.let {
            when {
                it.contains("image", true) -> {
                    var intent = Intent(context, ImageAndVideoViewer::class.java)
                    intent.putExtra(
                        Constants.IntentKeys.CONTENT,
                        viewmodel.getLocalFilePath(position)
                    )
                    intent.putExtra(Constants.IntentKeys.ISVIDEO, false)
                    context.startActivity(intent)
                }
                it.contains("video", true) -> {
                    var intent = Intent(context, ImageAndVideoViewer::class.java)
                    intent.putExtra(
                        Constants.IntentKeys.CONTENT,
                        viewmodel.getLocalFilePath(position)
                    )
                    intent.putExtra(Constants.IntentKeys.ISVIDEO, true)
                    context.startActivity(intent)
                }
                else -> {


                    var intent = getViewIntent(
                        FileProvider.getUriForFile(
                            context,
                            context.applicationContext.packageName + ".provider",
                            file
                        )
                    )

                    val chooser =
                        Intent.createChooser(intent, "Choose an application to open with:")
                    context.startActivity(chooser)


                }
            }
        }

    }

    companion object {

        private val DIFF_CALLBACK: DiffUtil.ItemCallback<ChatMessagesSchema> =
            object : DiffUtil.ItemCallback<ChatMessagesSchema>() {
                override fun areItemsTheSame(
                    oldItem: ChatMessagesSchema,
                    newItem: ChatMessagesSchema
                ): Boolean {
                    return oldItem.messageId == newItem.messageId
                }


                override fun areContentsTheSame(
                    oldItem: ChatMessagesSchema,
                    newItem: ChatMessagesSchema
                ): Boolean {

                    return false
//                    return Objects.equals(oldItem, newItem)
//                    return oldItem.messageId == newItem.messageId &&
//                            oldItem.message == newItem.message &&
//                            oldItem.messageTime == newItem.messageTime &&
//                            oldItem.mediaUrl == newItem.mediaUrl &&
//                            oldItem.mediaThumbUrl == newItem.mediaThumbUrl &&
//                            oldItem.isEdited == newItem.isEdited &&
//                            oldItem.isSender == newItem.isSender &&
//                            oldItem.isSaved == newItem.isSaved &&
//                            oldItem.messageStatus == newItem.messageStatus &&
//                            oldItem.isUploaded == newItem.isUploaded &&
//                            oldItem.isMediaCancelledByUser == newItem.isMediaCancelledByUser
                }
            }
    }

}

