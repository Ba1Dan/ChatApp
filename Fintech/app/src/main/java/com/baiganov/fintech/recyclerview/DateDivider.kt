package com.baiganov.fintech.recyclerview

import com.baiganov.fintech.R
import com.baiganov.fintech.model.Date

class DateDivider(val date: Date) : Item {
    override val id: String = date.date
    override val viewType: Int = R.layout.date_divider
}