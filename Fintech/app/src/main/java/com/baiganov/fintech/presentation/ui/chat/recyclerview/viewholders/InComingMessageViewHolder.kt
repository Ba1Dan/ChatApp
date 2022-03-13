package com.baiganov.fintech.presentation.ui.chat.recyclerview.viewholders

import android.view.View
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.presentation.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.view.MessageViewGroup
import com.baiganov.fintech.presentation.view.OnClickMessage
import com.baiganov.fintech.util.formatDate
import com.baiganov.fintech.util.parseHtml

class InComingMessageViewHolder(
    private val clickListener: OnClickMessage,
    itemView: View
) : BaseViewHolder<MessageFingerPrint>(itemView) {

    private val viewGroup: MessageViewGroup = itemView.findViewById(R.id.incoming_message)

    override fun bind(item: MessageFingerPrint) {
        val message = item.message
        viewGroup.apply {
            setReactions(
                message.reactions,
                clickListener,
                message.id
            )
            text = parseHtml(message.content)
            author = message.senderFullName
            date = formatDate(message.timestamp)

            addReactionByButton(clickListener, message.id)
            setAvatar(item.message.avatarUrl)
        }

        itemView.setOnLongClickListener {
            clickListener.onItemClick(TypeClick.OpenActionDialog(message))
            return@setOnLongClickListener true
        }
    }
}