package com.baiganov.fintech.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
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
        onResultListener.sendData(idMessage, emoji)
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

        adapter.setData(
            listOf(
                "\uD83D\uDE00",
                "\uD83D\uDE03",
                "\uD83D\uDE04",
                "\uD83D\uDE01",
                "\uD83D\uDE06",
                "\uD83D\uDE05",
                "\uD83E\uDD23",
                "\uD83D\uDE09",
                "\uD83D\uDE0A",
                "\uD83D\uDE0B",
                "\uD83D\uDE1C",
                "\uD83E\uDD14",
                "\uD83D\uDE0C",
                "\uD83D\uDE37",
            )
        )

        with(recyclerView) {
            val spanCount = (layoutManager as GridLayoutManager).spanCount
            val spacing = resources.getDimension(R.dimen.padding_normal).roundToInt()
            addItemDecoration(EmojiGridItemDecoration(spanCount, spacing))
        }
    }

    companion object {
        fun newInstance(messageId: Int) = EmojiBottomSheetDialog().apply {
            val bundle = Bundle()
            bundle.putInt(ARGUMENT_MESSAGE_ID, messageId)
            arguments = bundle
        }

        private const val ARGUMENT_MESSAGE_ID = "message_id"
    }
}

interface OnResultListener {
    fun sendData(position: Int?, emoji: String)
}