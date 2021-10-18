package com.baiganov.fintech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.bottomsheet.OnResultListener
import com.baiganov.fintech.model.Content
import com.baiganov.fintech.model.Date
import com.baiganov.fintech.model.Item
import com.baiganov.fintech.model.Reaction
import com.baiganov.fintech.recyclerview.ClickListener
import com.baiganov.fintech.recyclerview.MessageAdapter
import com.baiganov.fintech.сustomview.MessageViewGroup

class MainActivity : AppCompatActivity(), ClickListener, OnResultListener {

    private lateinit var adapter: MessageAdapter


    override fun sendData(position: Int?, emoji: String) {

        if (position != null) {
            val data = getData()
            data.forEach { item ->
                if (item is Content && item.id == position) {
                    item.reactions.add(Reaction(emoji, 1))
                }
            }

            adapter.setData(data)
        } else {

        }
    }

    override fun itemClick(position: Int) {
        val emojiBottomSheetDialog = EmojiBottomSheetDialog()
        val bundle = Bundle()
        bundle.putInt("id_message", position)
        emojiBottomSheetDialog.arguments = bundle
        emojiBottomSheetDialog.show(supportFragmentManager, emojiBottomSheetDialog.tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.rv_chat)
        adapter = MessageAdapter(this)
        rv.adapter = adapter

        adapter.setData(getData())
    }

    private fun getData(): MutableList<Item> {
        val data = mutableListOf<Item>(
            Date(
                "17 Окт"
            ),
            Content(
                0, 0, "Данияр", "Привет", mutableListOf(
                    Reaction("\uD83D\uDE09", 2),
                    Reaction("\uD83D\uDE09", 3),
                    Reaction("\uD83D\uDE09", 10),
                )
            ),
            Content(
                1, 1, "Данияр", "Салам", mutableListOf(
                    Reaction("\uD83D\uDE09", 2),
                    Reaction("\uD83D\uDE09", 3),
                    Reaction("\uD83D\uDE09", 10),
                )
            )
        )
        return data
    }
}