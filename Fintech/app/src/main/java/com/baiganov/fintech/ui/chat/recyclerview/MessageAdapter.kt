package com.baiganov.fintech.ui.chat.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.chat.recyclerview.viewholders.DateDividerViewHolder
import com.baiganov.fintech.ui.chat.recyclerview.viewholders.InComingMessageViewHolder
import com.baiganov.fintech.ui.chat.recyclerview.viewholders.OutGoingMessageViewHolder
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class MessageAdapter(private val clickListener: ItemClickListener) : RecyclerView.Adapter<BaseViewHolder<ItemFingerPrint>>() {

    private val differ = AsyncListDiffer(this, MessageDiffUtil());

    var messages: List<ItemFingerPrint>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ItemFingerPrint> {
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
        } as BaseViewHolder<ItemFingerPrint>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemFingerPrint>, position: Int) {
        when(getItemViewType(position)) {
            R.layout.outgoing_message -> {
                (holder as OutGoingMessageViewHolder).bind(messages[position] as MessageFingerPrint)
            }
            R.layout.incoming_message -> {
                (holder as InComingMessageViewHolder).bind(messages[position] as MessageFingerPrint)
            }
            R.layout.date_divider -> {
                (holder as DateDividerViewHolder).bind(messages[position] as DateDividerFingerPrint)
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