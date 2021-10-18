package com.baiganov.fintech.bottomsheet

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt

class EmojiBottomSheetDialog : BottomSheetDialogFragment(), EmojiClickListener {

    private lateinit var onResultListener: OnResultListener
    private var idMessage: Int? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        onResultListener = context as OnResultListener

    }

    override fun emojiClick(emoji: String) {
        onResultListener.sendData(idMessage, emoji)
        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.emoji_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_emoji_bottom)
        val args = arguments
        if (args != null) {
            idMessage = args.getInt("id_message")
        }

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
//
//        with(recyclerView) {
//            val spanCount = (layoutManager as GridLayoutManager).spanCount
//            val spacing = resources.getDimension(R.dimen.padding_medium).roundToInt()
//            addItemDecoration(EmojiGridItemDecoration(spanCount, spacing))
//        }

    }
}

interface OnResultListener {
    fun sendData(position: Int?, emoji: String)
}