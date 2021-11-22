package com.baiganov.fintech.ui.chat.recyclerview.viewholders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.сustomview.FlexBoxLayout
import com.baiganov.fintech.сustomview.OnClickMessage
import org.jsoup.Jsoup

class OutGoingMessageViewHolder(itemView: View, private val clickListener: OnClickMessage) :
    BaseViewHolder<MessageFingerPrint>(itemView) {

    private val content: TextView = itemView.findViewById(R.id.message_text_outgoing)
    private val btnAddReaction: ImageButton = itemView.findViewById(R.id.add_reaction_button_outgoing)
    private val flexBoxLayout: FlexBoxLayout =
        itemView.findViewById(R.id.flexbox_reactions_outgoing)

    override fun bind(item: MessageFingerPrint) {

        val message = item.message
        content.text = Jsoup.parse(message.content).text()
        flexBoxLayout.setReactions(message.reactions, clickListener, message.id)

        btnAddReaction.setOnClickListener {
            clickListener.onItemClick(TypeClick.OpenBottomSheet(message.id))
        }
        itemView.setOnLongClickListener {
            clickListener.onItemClick(TypeClick.OpenActionDialog(message.id))
            return@setOnLongClickListener true
        }
    }
}