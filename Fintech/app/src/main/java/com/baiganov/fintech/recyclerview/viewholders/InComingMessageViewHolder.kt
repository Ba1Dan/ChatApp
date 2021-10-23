package com.baiganov.fintech.recyclerview.viewholders

import android.view.View
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.recyclerview.BaseViewHolder
import com.baiganov.fintech.recyclerview.ClickListener
import com.baiganov.fintech.recyclerview.Message
import com.baiganov.fintech.сustomview.MessageViewGroup


class InComingMessageViewHolder(itemView: View, private val clickListener: ClickListener) : BaseViewHolder<Message>(itemView) {

    private val viewGroup: MessageViewGroup = itemView.findViewById(R.id.incoming_message)
    private val txt: TextView = viewGroup.findViewById(R.id.message_text_incoming)

    override fun bind(item: Message) {
        val content = item.content
        viewGroup.apply {
            setReactions(
                content.reactions
            )
            text = content.text
            author = content.name
        }

        txt.setOnLongClickListener {
            clickListener.itemClick(content.id)
            return@setOnLongClickListener true
        }
    }
}