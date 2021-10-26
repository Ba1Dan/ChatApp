package com.baiganov.fintech.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.User
import com.baiganov.fintech.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.model.Content
import com.baiganov.fintech.model.Date
import com.baiganov.fintech.model.Reaction
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity(), ItemClickListener, OnResultListener {

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val rv = findViewById<RecyclerView>(R.id.rv_chat)
        adapter = MessageAdapter(this)
        rv.adapter = adapter

        val btnSend = findViewById<FloatingActionButton>(R.id.btn_send)
        val inputMessage = findViewById<EditText>(R.id.input_message)
        val btnAddFile = findViewById<ImageButton>(R.id.btn_add_file)

        btnSend.setOnClickListener {
            data = ArrayList(data)
            data.add(
                MessageFingerPrint(
                    Content(
                        id++, User.getId(), "Данияр", inputMessage.text.toString(), mutableListOf()
                    )
                )
            )
            inputMessage.setText("")
            adapter.messages = data
        }

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

        adapter.messages = data
    }

    override fun sendData(position: Int?, emoji: String) {

        position?.let {
            data = ArrayList(data)
            for (i in data.indices) {
                val item = data[i]
                if (item is MessageFingerPrint && item.content.id == position) {
                    val reactions = ArrayList(item.content.reactions.map { it.copy() })
                    reactions.add(Reaction(User.getId(), emoji, 1))
                    val content = item.content.copy(reactions = reactions)
                    val message = MessageFingerPrint(content)
                    data[i] = message
                }
            }
            adapter.messages = data
        }
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        EmojiBottomSheetDialog.newInstance(position).show(supportFragmentManager, null)
    }

    private var id: Int = 2
    private var data = mutableListOf<ItemFingerPrint>(
        DateDividerFingerPrint(
            Date(
                "17 Окт"
            )
        ),
        MessageFingerPrint(
            Content(
                0, 0, "Данияр", "Привет", mutableListOf(
                    Reaction(1, "\uD83D\uDE09", 2),
                    Reaction(1, "\uD83D\uDE09", 3),
                    Reaction(1, "\uD83D\uDE09", 10),
                )
            )
        ),
        MessageFingerPrint(
            Content(
                1, 1, "Данияр", "Привет", mutableListOf(
                    Reaction(2, "\uD83D\uDE09", 2),
                    Reaction(2, "\uD83D\uDE09", 3),
                    Reaction(2, "\uD83D\uDE09", 10),
                )
            )
        )
    )
}