package com.baiganov.fintech.ui.chat.recyclerview.viewholders

import android.view.View
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.сustomview.MessageViewGroup


class InComingMessageViewHolder(itemView: View, private val clickListener: ItemClickListener) : BaseViewHolder<MessageFingerPrint>(itemView) {

    private val viewGroup: MessageViewGroup = itemView.findViewById(R.id.incoming_message)
    private val txt: TextView = viewGroup.findViewById(R.id.message_text_incoming)

    override fun bind(item: MessageFingerPrint) {
        val content = item.content
        viewGroup.apply {
            setReactions(
                content.reactions
            )
            text = content.text
            author = content.name
            addReactionByButton(clickListener, content.id, item)
        }

        txt.setOnLongClickListener {
            clickListener.onItemClick(content.id, item)
            return@setOnLongClickListener true
        }
    }
}