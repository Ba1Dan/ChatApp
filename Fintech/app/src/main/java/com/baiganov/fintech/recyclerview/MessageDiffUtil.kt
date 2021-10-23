package com.baiganov.fintech.recyclerview

import androidx.recyclerview.widget.DiffUtil

class MessageDiffUtil : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return when {
            oldItem is Message && newItem is Message -> oldItem.content == newItem.content
            oldItem is DateDivider && newItem is DateDivider -> oldItem.date == newItem.date
            else -> true
        }
    }
}