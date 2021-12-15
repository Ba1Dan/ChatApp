package com.baiganov.fintech.presentation.ui.chat.recyclerview

import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.StreamFingerPrint

interface ItemClickListener {

    fun onItemClick(click: TypeItemClickStream)

}

sealed interface TypeItemClickStream {

    class OpenStream(val stream: StreamEntity) : TypeItemClickStream

    class ClickSteam(val position: Int, val item: ItemFingerPrint) : TypeItemClickStream
}