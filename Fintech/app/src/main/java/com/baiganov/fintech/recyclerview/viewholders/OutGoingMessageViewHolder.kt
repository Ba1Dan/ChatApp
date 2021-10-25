package com.baiganov.fintech.recyclerview.viewholders

import android.util.Log
import android.view.View
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.recyclerview.BaseViewHolder
import com.baiganov.fintech.recyclerview.ClickListener
import com.baiganov.fintech.recyclerview.Message
import com.baiganov.fintech.сustomview.FlexBoxLayout


class OutGoingMessageViewHolder(itemView: View, private val clickListener: ClickListener) :
    BaseViewHolder<Message>(itemView) {

    private val txt: TextView = itemView.findViewById(R.id.message_text_outgoing)
    private val flexBoxLayout: FlexBoxLayout =
        itemView.findViewById(R.id.flexbox_reactions_outgoing)

    override fun bind(item: Message) {

        val content = item.content
        txt.text = content.text
        Log.d("adding", "holder ${content.reactions.size}")
        flexBoxLayout.setReactions(content.reactions)

        txt.setOnLongClickListener {
            clickListener.itemClick(content.id)
            return@setOnLongClickListener true
        }
    }
}