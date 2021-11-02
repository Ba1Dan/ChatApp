package com.baiganov.fintech.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.DataManager
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.ui.chat.recyclerview.MessageAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity(), ItemClickListener, OnResultListener {

    private lateinit var adapter: MessageAdapter
    private lateinit var toolBarChat: Toolbar
    private lateinit var tvTopic: TextView
    private lateinit var dataManager: DataManager
    private lateinit var btnSend: FloatingActionButton
    private lateinit var inputMessage: EditText
    private lateinit var btnAddFile: ImageButton
    private lateinit var rvChat: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()

        val titleStream = intent.extras?.getString(ARG_TITLE_STREAM) ?: EMPTY_STRING
        val titleTopic = intent.extras?.getString(ARG_TITLE_TOPIC) ?: EMPTY_STRING
        dataManager = DataManager()
        adapter = MessageAdapter(this)
        rvChat.adapter = adapter
        toolBarChat.title = titleStream
        tvTopic.text = this.getString(R.string.title_topic, titleTopic)
        adapter.messages = dataManager.messages

        setClickListener()
    }

    override fun sendData(position: Int?, emoji: String) {
        position?.let {
            adapter.messages = dataManager.addEmoji(position, emoji)
        }
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        EmojiBottomSheetDialog.newInstance(position).show(supportFragmentManager, null)
    }

    private fun initViews() {
        rvChat = findViewById(R.id.rv_chat)

        btnSend = findViewById(R.id.btn_send)
        inputMessage = findViewById(R.id.input_message)
        btnAddFile = findViewById(R.id.btn_add_file)
        toolBarChat = findViewById(R.id.toolbar_chat)
        tvTopic = findViewById(R.id.tv_topic)
    }

    private fun setClickListener() {
        btnSend.setOnClickListener {
            adapter.messages = dataManager.addMessage(inputMessage.text.toString())
            rvChat.smoothScrollToPosition(dataManager.messages.size - 1)
            inputMessage.setText("")
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

        toolBarChat.setNavigationOnClickListener { finish() }
    }

    companion object {

        private const val ARG_TITLE_STREAM = "title_stream"
        private const val ARG_TITLE_TOPIC = "title_topic"
        private const val ARG_ID_TOPIC = "id_topic"
        private const val EMPTY_STRING = ""

        fun createIntent(context: Context, item: TopicFingerPrint): Intent {
            return Intent(context, ChatActivity::class.java)
                .putExtra(ARG_TITLE_STREAM, item.streamTitle)
                .putExtra(ARG_ID_TOPIC, item.topic.id)
                .putExtra(ARG_TITLE_TOPIC, item.topic.title)

        }
    }
}