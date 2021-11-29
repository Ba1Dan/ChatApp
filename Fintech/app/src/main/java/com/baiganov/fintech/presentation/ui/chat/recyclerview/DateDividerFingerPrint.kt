package com.baiganov.fintech.presentation.ui.chat.recyclerview

import com.baiganov.fintech.R
import com.baiganov.fintech.model.Date
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint

class DateDividerFingerPrint(val date: String) : ItemFingerPrint {
    override val id: Int = 0
    override val viewType: Int = R.layout.date_divider
}