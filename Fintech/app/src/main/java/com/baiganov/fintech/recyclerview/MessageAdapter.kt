package com.baiganov.fintech.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.User
import com.baiganov.fintech.model.Item
import com.baiganov.fintech.model.Content
import com.baiganov.fintech.model.Date
import com.baiganov.fintech.сustomview.FlexBoxLayout
import com.baiganov.fintech.сustomview.MessageViewGroup

class MessageAdapter(private val clickListener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages = mutableListOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.incoming_message -> {
                InComingMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.incoming_message, parent, false),
                    clickListener
                )
            }

            R.layout.outcoming_message -> {
                OutGoingMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.outcoming_message, parent, false),
                    clickListener
                )
            }
            R.layout.date_divider -> {
                DateDividerViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.date_divider, parent, false)
                )
            }
            else -> {
                throw Exception("Unknown ViewType ${parent.resources.getResourceName(viewType)}")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            R.layout.outcoming_message -> {
                (holder as OutGoingMessageViewHolder).bind(messages[position] as Content)
            }
            R.layout.incoming_message -> {
                (holder as InComingMessageViewHolder).bind(messages[position] as Content)
            }
            R.layout.date_divider -> {
                (holder as DateDividerViewHolder).bind(messages[position] as Date)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position]) {
            is Content -> {
                val content = messages[position] as Content
                if (content.userId == User.getId()) {
                    R.layout.outcoming_message
                } else {
                    R.layout.incoming_message
                }
            }
            is Date -> {
                R.layout.date_divider
            }
            else -> {
                -1
            }
        }
    }

    fun setData(newData: MutableList<Item>) {
        messages = newData
        notifyDataSetChanged()
    }

    class InComingMessageViewHolder(itemView: View, private val clickListener: ClickListener) : RecyclerView.ViewHolder(itemView) {

        private val viewGroup: MessageViewGroup = itemView.findViewById(R.id.incoming_message)
        private val txt: TextView = viewGroup.findViewById(R.id.message_text_incoming)

        fun bind(content: Content) {
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

    class OutGoingMessageViewHolder(itemView: View, private val clickListener: ClickListener) : RecyclerView.ViewHolder(itemView) {

        private val txt: TextView = itemView.findViewById(R.id.message_text_outgoing)
        private val flexBoxLayout: FlexBoxLayout =
            itemView.findViewById(R.id.flexbox_reactions_outgoing)

        fun bind(content: Content) {
            txt.text = content.text
            flexBoxLayout.setReactions(content.reactions)

            txt.setOnLongClickListener {
                clickListener.itemClick(content.id)
                return@setOnLongClickListener true
            }
        }
    }

    class DateDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val txt: TextView = itemView.findViewById(R.id.txt_date_divider)

        fun bind(date: Date) {
            txt.text = date.date
        }
    }
}