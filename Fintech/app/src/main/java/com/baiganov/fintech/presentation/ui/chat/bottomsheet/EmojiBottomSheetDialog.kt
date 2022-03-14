package com.baiganov.fintech.presentation.ui.chat.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.model.Emoji
import com.baiganov.fintech.presentation.util.EmojiEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

class EmojiBottomSheetDialog : BottomSheetDialogFragment(), EmojiClickListener {

    private lateinit var onResultListener: OnResultListener
    private var idMessage: Int? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onResultListener = context as OnResultListener
    }

    override fun emojiClick(emoji: String) {
        onResultListener.sendData(TypeClick.AddReaction(idMessage, emoji))
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.emoji_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_emoji_bottom)

        idMessage = arguments?.getInt(ARGUMENT_MESSAGE_ID)

        val adapter = EmojiAdapter(this)
        recyclerView.adapter = adapter

        adapter.emojis = (getEmoji())

        with(recyclerView) {
            val spanCount = (layoutManager as GridLayoutManager).spanCount
            val spacing = resources.getDimension(R.dimen.padding_normal).roundToInt()
            addItemDecoration(EmojiGridItemDecoration(spanCount, spacing))
        }
    }

    private fun getEmoji(): List<Emoji> {
        return EmojiEnum.values().map { Emoji(it.unicodeCodePoint, it.nameInZulip) }
    }

    companion object {

        private const val ARGUMENT_MESSAGE_ID = "message_id"

        fun newInstance(messageId: Int) = EmojiBottomSheetDialog().apply {
            arguments = bundleOf(ARGUMENT_MESSAGE_ID to messageId)
        }
    }
}

interface OnResultListener {
    fun sendData(click: TypeClick)
}