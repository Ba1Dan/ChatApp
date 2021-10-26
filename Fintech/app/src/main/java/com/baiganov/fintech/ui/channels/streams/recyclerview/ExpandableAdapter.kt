package com.baiganov.fintech.ui.channels.streams.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener

class ExpandableAdapter(private val clickListener: ItemClickListener) :
    RecyclerView.Adapter<BaseViewHolder<ItemFingerPrint>>() {

    var dataOfList: List<ItemFingerPrint>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val differ = AsyncListDiffer(this, StreamDiffCallback())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemFingerPrint> {
        return when (viewType) {
            R.layout.item_stream -> {
                StreamViewHolder(
                    clickListener,
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_stream, parent, false),
                    )
            }

            R.layout.item_topic -> {
                TopicViewHolder(
                    clickListener,
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_topic, parent, false),
                )
            }
            else -> {
                throw Exception("Unknown ViewType ${parent.resources.getResourceName(viewType)}")
            }
        } as BaseViewHolder<ItemFingerPrint>

    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemFingerPrint>, position: Int) {

        when (dataOfList[position].viewType) {
            R.layout.item_stream -> {
                (holder as StreamViewHolder).apply {
                    bind(dataOfList[position] as StreamFingerPrint)
                }
            }
            R.layout.item_topic -> {
                (holder as TopicViewHolder).apply {
                    bind(dataOfList[position] as TopicFingerPrint)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataOfList.size
    }

    override fun getItemViewType(position: Int): Int {
        return dataOfList[position].viewType
    }

    class StreamViewHolder(private val clickListener: ItemClickListener, itemView: View) :
        BaseViewHolder<StreamFingerPrint>(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

        override fun bind(item: StreamFingerPrint) {
            tvTitle.text = item.stream.name
            itemView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                clickListener.onItemClick(adapterPosition, item)
            }
        }
    }

    class TopicViewHolder(private val clickListener: ItemClickListener, itemView: View) :
        BaseViewHolder<TopicFingerPrint>(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.title)

        override fun bind(item: TopicFingerPrint) {
            tvTitle.text = item.topic.title
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition, item)
            }
        }
    }
}