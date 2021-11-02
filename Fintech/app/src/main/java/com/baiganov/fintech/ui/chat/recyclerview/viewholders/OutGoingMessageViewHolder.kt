package com.baiganov.fintech.ui.chat.recyclerview.viewholders

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.сustomview.FlexBoxLayout


class OutGoingMessageViewHolder(itemView: View, private val clickListener: ItemClickListener) :
    BaseViewHolder<MessageFingerPrint>(itemView) {

    private val txt: TextView = itemView.findViewById(R.id.message_text_outgoing)
    private val btnAddReaction: ImageButton = itemView.findViewById(R.id.add_reaction_button_outgoing)
    private val flexBoxLayout: FlexBoxLayout =
        itemView.findViewById(R.id.flexbox_reactions_outgoing)

    override fun bind(item: MessageFingerPrint) {

        val content = item.content
        txt.text = content.text
        Log.d("adding", "holder ${content.reactions.size}")
        flexBoxLayout.setReactions(content.reactions)

        btnAddReaction.setOnClickListener {
            clickListener.onItemClick(content.id, item)
        }
        txt.setOnLongClickListener {
            clickListener.onItemClick(content.id, item)
            return@setOnLongClickListener true
        }
    }
}