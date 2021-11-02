package com.baiganov.fintech.ui.chat.recyclerview

import com.baiganov.fintech.R
import com.baiganov.fintech.model.Date
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class DateDividerFingerPrint(val date: Date) : ItemFingerPrint {
    override val id: String = date.date
    override val viewType: Int = R.layout.date_divider
}