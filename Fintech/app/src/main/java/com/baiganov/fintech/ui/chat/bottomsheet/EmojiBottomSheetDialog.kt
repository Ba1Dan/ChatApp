package com.baiganov.fintech.ui.chat.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.model.Emoji
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
        onResultListener.sendData(TypeActionMessage.AddReaction(idMessage, emoji))
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
                Emoji(0x1f47e, "space_invader"),
                Emoji(0x1f52c, "scientist"),
                Emoji(0x1f6d2, "shopping_cart"),
                Emoji(0x1f3d7, "construction"),
                Emoji(0x1f58b, "fountain_pen"),
                Emoji(0x1f36f, "honey"),
                Emoji(0x1f48b, "lipstick_kiss"),
                Emoji(0x1f699, "jeep"),
                Emoji(0x1f6b0, "potable_water"),
                Emoji(0x1f379, "tropical_drink"),
                Emoji(0x1f62f, "hushed"),
                Emoji(0x1f4db, "name_badge"),
                Emoji(0x1f947, "number_one"),
                Emoji(0x1f632, "astonished"),
                Emoji(0x1f4d5, "red_book"),
                Emoji(0x1f4d8, "blue_book"),
                Emoji(0x2620, "pirate"),
                Emoji(0x25fb, "white_medium_square"),
                Emoji(0x1f94b, "black_belt"),
                Emoji(0x1f3d3, "ping_pong"),
                Emoji(0x231b, "times_up"),
                Emoji(0x1f4ed, "empty_mailbox"),
                Emoji(0x26a0, "warning"),
                Emoji(0x1f353, "strawberry"),
                Emoji(0x1f6b8, "children_crossing"),
                Emoji(0x1f600, "grinning"),
                Emoji(0x1f527, "wrench"),
                Emoji(0x1f483, "dancer"),
                Emoji(0x1f636, "blank"),
                Emoji(0x2620, "poison"),
                Emoji(0x1f494, "heartache"),
                Emoji(0x1f344, "mushroom"),
                Emoji(0x1f916, "robot"),
                Emoji(0x1f381, "present"),
                Emoji(0x1f54e, "menorah"),
                Emoji(0x1f534, "red_circle"),
                Emoji(0x1f60f, "smug"),
                Emoji(0x1f513, "unlocked"),
                Emoji(0x1f93a, "fencing"),
                Emoji(0x1f595, "middle_finger"),
                Emoji(0x1f46d, "two_women_holding_hands"),
                Emoji(0x1f5e3, "speaking_head"),
                Emoji(0x1f6ab, "not_allowed"),
                Emoji(0x1f575, "detective"),
                Emoji(0x1f392, "satchel"),
                Emoji(0x2615, "coffee"),
                Emoji(0x1f449, "point_right"),
                Emoji(0x1f372, "stew"),
                Emoji(0x1f37d, "meal"),
                Emoji(0x1f4b3, "debit_card"),
                Emoji(0x1f6cc, "guestrooms"),
                Emoji(0x1f481, "person_tipping_hand"),
                Emoji(0x1f360, "sweet_potato"),

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
    fun sendData(action: TypeActionMessage)
}