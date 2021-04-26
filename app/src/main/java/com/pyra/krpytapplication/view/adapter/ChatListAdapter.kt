package com.pyra.krpytapplication.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.pyra.krpytapplication.R
import com.pyra.krpytapplication.Utils.Constants
import com.pyra.krpytapplication.Utils.Constants.IntentKeys.DISPLAY_NAME
import com.pyra.krpytapplication.Utils.Constants.IntentKeys.ISGROUP
import com.pyra.krpytapplication.Utils.Constants.IntentKeys.KRYPTKEY
import com.pyra.krpytapplication.Utils.Constants.IntentKeys.ROOMID
import com.pyra.krpytapplication.Utils.hide
import com.pyra.krpytapplication.Utils.loadImage
import com.pyra.krpytapplication.Utils.show
import com.pyra.krpytapplication.databinding.ItemChatsBinding
import com.pyra.krpytapplication.view.activity.ChatActivity
import com.pyra.krpytapplication.viewmodel.ChatListViewModel
import fetchThemeColor


class ChatListAdapter(
    private val context: Context,
    var viewmodel: ChatListViewModel
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            R.layout.item_chats,
            parent,
            false
        )
        return MyViewHolder(binding as ItemChatsBinding)
    }

    override fun getItemCount(): Int {
        return viewmodel.getNumberOfChatList()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder: MyViewHolder = holder as MyViewHolder

        if (viewmodel.getUnreadMessagesCount(position) == "") {
            viewHolder.binding.lastMsgTime.setTextColor(
                fetchThemeColor(
                    R.attr.sub_title_color,
                    context
                )
            )
            viewHolder.binding.unreadCountText.hide()
        } else {
            viewHolder.binding.lastMsgTime.setTextColor(
                fetchThemeColor(
                    R.attr.unread_green_color,
                    context
                )
            )
            viewHolder.binding.unreadCountText.show()
        }
        viewHolder.binding.lastMessage.text = viewmodel.getLastMessage(position)
        viewHolder.binding.lastMsgTime.text = viewmodel.getLastMessageTime(position)
        viewHolder.binding.removeViewCheck.hide()
        viewHolder.binding.unreadCountText.text = viewmodel.getUnreadMessagesCount(position)
        viewHolder.binding.userName.text = viewmodel.getChatListDisplayName(position)
        viewHolder.binding.userImage.loadImage(viewmodel.getChatListImage(position))

        viewHolder.itemView.setOnClickListener {
            if (viewmodel.isMultiSelectionEnabled) {
                viewmodel.makeSelection(position)
            } else {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(KRYPTKEY, viewmodel.getChatListKryptId(position))
                intent.putExtra(ROOMID, viewmodel.getChatListRoomId(position))
                intent.putExtra(ISGROUP, viewmodel.isGroupChat(position))
                intent.putExtra(DISPLAY_NAME, viewmodel.getChatListDisplayName(position))
                intent.putExtra(
                    Constants.IntentKeys.IS_ADDED_TO_CONTACTS,
                    viewmodel.isAlreadyAddedToContacts(position)
                )
                context.startActivity(intent)
            }
        }

        viewHolder.itemView.setOnLongClickListener {
            viewmodel.makeSelection(position)
            true
        }

        if (viewmodel.getIsSelected(position)) {
            holder.binding.parentView.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.selectedChatColor
                )
            )
            holder.binding.chatSelected.show()
        } else {
            holder.binding.parentView.setBackgroundColor(
                fetchThemeColor(R.attr.page_default_bg, context)
            )
            holder.binding.chatSelected.hide()
        }

    }


    inner class MyViewHolder(itemView: ItemChatsBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: ItemChatsBinding = itemView


    }

}