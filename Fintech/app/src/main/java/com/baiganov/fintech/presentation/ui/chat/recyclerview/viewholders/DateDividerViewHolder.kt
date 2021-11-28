package com.baiganov.fintech.presentation.ui.chat.recyclerview.viewholders

import android.view.View
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.chat.recyclerview.BaseViewHolder
import com.baiganov.fintech.presentation.ui.chat.recyclerview.DateDividerFingerPrint


class DateDividerViewHolder(itemView: View) : BaseViewHolder<DateDividerFingerPrint>(itemView) {

    private val txt: TextView = itemView.findViewById(R.id.txt_date_divider)

    override fun bind(item: DateDividerFingerPrint) {
        val date = item.date
        txt.text = date.date
    }
}