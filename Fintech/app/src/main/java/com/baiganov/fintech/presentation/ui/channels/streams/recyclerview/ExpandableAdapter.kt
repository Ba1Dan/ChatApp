package com.baiganov.fintech.presentation.ui.channels.streams.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.StreamFingerPrint
import com.baiganov.fintech.presentation.model.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.presentation.ui.chat.recyclerview.TypeItemClickStream
import com.google.android.material.button.MaterialButton

class ExpandableAdapter(private val clickListener: ItemClickListener) :
    RecyclerView.Adapter<BaseViewHolder<ItemFingerPrint>>() {

    private val differ = AsyncListDiffer(this, StreamDiffCallback)

    var dataOfList: List<ItemFingerPrint>
        get() = differ.currentList
        set(value) = differ.submitList(value)

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
                if (position % 2 == 0) {
                    holder.itemView.apply {
                        setBackgroundColor(context.getColor(R.color.yellow))
                    }
                } else {
                    holder.itemView.apply {
                        setBackgroundColor(context.getColor(R.color.green))
                    }
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
        private val btnOpen: MaterialButton = itemView.findViewById(R.id.btn_open)
        private val btnOpenStream: MaterialButton = itemView.findViewById(R.id.btn_open_stream)

        override fun bind(item: StreamFingerPrint) {
            tvTitle.text = itemView.context.getString(R.string.title_topic_percent, item.stream.name)

            if (item.isExpanded) {
                btnOpen.setIconResource(R.drawable.ic_arrow_up)
            } else {
                btnOpen.setIconResource(R.drawable.ic_arrow)
            }

            itemView.setOnClickListener {
                item.isExpanded = !item.isExpanded
                Log.d("gett", "click open")
                clickListener.onItemClick(TypeItemClickStream.ClickSteam(adapterPosition, item))
            }

            btnOpenStream.setOnClickListener {
                clickListener.onItemClick(TypeItemClickStream.OpenStream(item.stream))
            }
        }
    }

    class TopicViewHolder(private val clickListener: ItemClickListener, itemView: View) :
        BaseViewHolder<TopicFingerPrint>(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.title)

        override fun bind(item: TopicFingerPrint) {
            tvTitle.text = item.topic.title
            itemView.setOnClickListener {
                clickListener.onItemClick(TypeItemClickStream.ClickSteam(adapterPosition, item))
            }
        }
    }
}