package com.baiganov.fintech.presentation.ui.chat.recyclerview.viewholders

import android.view.View
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.presentation.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.сustomview.MessageViewGroup
import com.baiganov.fintech.presentation.сustomview.OnClickMessage
import com.baiganov.fintech.util.formatDate
import org.jsoup.Jsoup

class InComingMessageViewHolder(itemView: View, private val clickListener: OnClickMessage) : BaseViewHolder<MessageFingerPrint>(itemView) {

    private val viewGroup: MessageViewGroup = itemView.findViewById(R.id.incoming_message)

    override fun bind(item: MessageFingerPrint) {
        val message = item.message
        viewGroup.apply {
            setReactions(
                message.reactions,
                clickListener,
                message.id
            )
            text = Jsoup.parse(message.content).text()
            author = message.senderFullName
            date = formatDate(message.timestamp)
            addReactionByButton(clickListener, message.id, item)
        }

        itemView.setOnLongClickListener {
            clickListener.onItemClick(TypeClick.OpenActionDialog(message.id))
            return@setOnLongClickListener true
        }
    }
}