package com.baiganov.fintech.presentation.ui.chat.recyclerview.viewholders

import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.baiganov.fintech.R
import com.baiganov.fintech.di.NetworkModule
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.presentation.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.сustomview.FlexBoxLayout
import com.baiganov.fintech.presentation.сustomview.OnClickMessage
import com.baiganov.fintech.util.formatDate
import com.baiganov.fintech.util.parseHtml
import org.jsoup.Jsoup
import java.util.*

class OutGoingMessageViewHolder(itemView: View, private val clickListener: OnClickMessage) :
    BaseViewHolder<MessageFingerPrint>(itemView) {

    private val content: TextView = itemView.findViewById(R.id.message_text_outgoing)
    private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
    private val btnAddReaction: ImageButton = itemView.findViewById(R.id.add_reaction_button_outgoing)
    private val flexBoxLayout: FlexBoxLayout =
        itemView.findViewById(R.id.flexbox_reactions_outgoing)

    override fun bind(item: MessageFingerPrint) {

        val message = item.message

        content.text = parseHtml(message.content)
        content.movementMethod = LinkMovementMethod.getInstance()
        flexBoxLayout.setReactions(message.reactions, clickListener, message.id)

        btnAddReaction.setOnClickListener {
            clickListener.onItemClick(TypeClick.OpenBottomSheet(message.id))
        }

        tvDate.text = formatDate(item.message.timestamp)

        content.setOnLongClickListener {
            clickListener.onItemClick(TypeClick.OpenActionDialog(message))
            return@setOnLongClickListener true
        }
    }
}