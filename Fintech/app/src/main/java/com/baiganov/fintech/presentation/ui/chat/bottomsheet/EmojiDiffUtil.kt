package com.baiganov.fintech.presentation.ui.chat.bottomsheet

import androidx.recyclerview.widget.DiffUtil
import com.baiganov.fintech.model.Emoji

class EmojiDiffUtil : DiffUtil.ItemCallback<Emoji>() {

    override fun areItemsTheSame(oldItem: Emoji, newItem: Emoji): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Emoji, newItem: Emoji): Boolean {
        return oldItem == newItem
    }
}
