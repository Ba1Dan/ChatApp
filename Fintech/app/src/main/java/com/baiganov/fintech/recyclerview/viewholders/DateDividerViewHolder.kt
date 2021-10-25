package com.baiganov.fintech.recyclerview.viewholders

import android.view.View
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.recyclerview.BaseViewHolder
import com.baiganov.fintech.recyclerview.DateDivider


class DateDividerViewHolder(itemView: View) : BaseViewHolder<DateDivider>(itemView) {

    private val txt: TextView = itemView.findViewById(R.id.txt_date_divider)

    override fun bind(item: DateDivider) {
        val date = item.date
        txt.text = date.date
    }
}