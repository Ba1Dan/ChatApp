package com.baiganov.fintech.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.recyclerview.viewholders.DateDividerViewHolder
import com.baiganov.fintech.recyclerview.viewholders.InComingMessageViewHolder
import com.baiganov.fintech.recyclerview.viewholders.OutGoingMessageViewHolder

class MessageAdapter(private val clickListener: ClickListener) : RecyclerView.Adapter<BaseViewHolder<Item>>() {

    private val differ = AsyncListDiffer(this, MessageDiffUtil());

    var messages: List<Item>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Item> {
        return when (viewType) {
            R.layout.incoming_message -> {
                InComingMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.incoming_message, parent, false),
                    clickListener
                )
            }

            R.layout.outgoing_message -> {
                OutGoingMessageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.outgoing_message, parent, false),
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
        } as BaseViewHolder<Item>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Item>, position: Int) {
        when(getItemViewType(position)) {
            R.layout.outgoing_message -> {
                (holder as OutGoingMessageViewHolder).bind(messages[position] as Message)
            }
            R.layout.incoming_message -> {
                (holder as InComingMessageViewHolder).bind(messages[position] as Message)
            }
            R.layout.date_divider -> {
                (holder as DateDividerViewHolder).bind(messages[position] as DateDivider)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].viewType
    }
}