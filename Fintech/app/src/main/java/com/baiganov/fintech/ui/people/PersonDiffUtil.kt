package com.baiganov.fintech.ui.people

import androidx.recyclerview.widget.DiffUtil

class PersonDiffUtil : DiffUtil.ItemCallback<UserFingerPrint>() {

    override fun areItemsTheSame(oldItem: UserFingerPrint, newItem: UserFingerPrint): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserFingerPrint, newItem: UserFingerPrint): Boolean {
        return oldItem.user == newItem.user
    }
}