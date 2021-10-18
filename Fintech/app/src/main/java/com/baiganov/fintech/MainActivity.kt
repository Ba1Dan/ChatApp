package com.baiganov.fintech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), ClickListener, OnResultListener {

    private lateinit var adapter: MessageAdapter


    override fun sendData(position: Int?, emoji: String) {

        if (position != null) {
            data.forEach { item ->
                if (item is Content && item.id == position) {
                    item.reactions.add(Reaction(User.getId(), emoji, 1))
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

        val btnSend = findViewById<FloatingActionButton>(R.id.btn_send)
        val inputMessage = findViewById<EditText>(R.id.input_message)
        val btnAddFile = findViewById<ImageButton>(R.id.btn_add_file)

        inputMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.toString().isEmpty()) {
                        btnAddFile.visibility = View.VISIBLE
                        btnSend.visibility = View.GONE
                    } else {
                        btnSend.visibility = View.VISIBLE
                        btnAddFile.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        btnSend.setOnClickListener {
            data.add(Content(
                id++, User.getId(), "Данияр", inputMessage.text.toString(), mutableListOf()
            ))
            inputMessage.setText("")
            adapter.setData(data)
        }

        adapter.setData(data)
    }


    private var id: Int = 2
    private val data = mutableListOf<Item>(
        Date(
            "17 Окт"
        ),
        Content(
            0, 0, "Данияр", "Привет", mutableListOf(
                Reaction(1, "\uD83D\uDE09", 2),
                Reaction(1, "\uD83D\uDE09", 3),
                Reaction(1, "\uD83D\uDE09", 10),
            )
        ),
        Content(
            1, 1, "Данияр", "Салам", mutableListOf(
                Reaction(2, "\uD83D\uDE09", 2),
                Reaction(2, "\uD83D\uDE09", 3),
                Reaction(2, "\uD83D\uDE09", 10),
            )
        )
    )
}