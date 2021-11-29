package com.baiganov.fintech.presentation.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.presentation.ui.chat.dialog.ActionDialog
import com.baiganov.fintech.presentation.ui.chat.recyclerview.MessageAdapter
import com.baiganov.fintech.presentation.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import com.baiganov.fintech.presentation.сustomview.OnClickMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class ChatActivity : MvpAppCompatActivity(), OnClickMessage, OnResultListener, ChatView {

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>

    private val presenter: ChatPresenter by moxyPresenter { presenterProvider.get() }
    private val component by lazy { (application as App).component }

    private lateinit var adapter: MessageAdapter
    private lateinit var toolBarChat: Toolbar
    private lateinit var tvTopic: TextView
    private lateinit var btnSend: FloatingActionButton
    private lateinit var inputMessage: EditText
    private lateinit var btnAddFile: ImageButton
    private lateinit var rvChat: RecyclerView

    private val streamTitle: String by lazy { intent.extras?.getString(ARG_TITLE_STREAM)!! }
    private val topicTitle: String by lazy { intent.extras?.getString(ARG_TITLE_TOPIC)!! }
    private val streamId: Int by lazy { intent.extras?.getInt(ARG_STREAM_ID)!! }

    private var positionRecyclerView: TypeScroll = TypeScroll.DOWN
    private var isLoadNewPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        setupText()
        setupRecyclerView()

        presenter.obtainEvent(
            Event.EventChat.LoadFirstMessages(
                streamTitle = streamTitle,
                topicTitle = topicTitle,
                streamId = streamId
            )
        )

        setClickListener()
    }

    override fun sendData(click: TypeClick) {
        when (click) {
            is TypeClick.AddReaction -> {
                click.messageId?.let {
                    presenter.obtainEvent(
                        Event.EventChat.AddReaction(
                            messageId = click.messageId,
                            emojiName = click.emoji,
                            streamTitle = streamTitle,
                            topicTitle = topicTitle
                        )
                    )
                }
            }

            is TypeClick.EditMessage -> {
                Toast.makeText(this, "edit message", Toast.LENGTH_SHORT).show()
            }

            is TypeClick.DeleteMessage -> {
                presenter.obtainEvent(
                    Event.EventChat.DeleteMessage(
                        streamTitle = streamTitle,
                        topicTitle = topicTitle,
                        messageId = click.messageId,
                    )
                )
                Toast.makeText(this, "deleted message", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.d(javaClass.simpleName, "unknown type click")
            }
        }
    }

    override fun onItemClick(click: TypeClick) {
        when(click) {
            is TypeClick.OpenBottomSheet -> {
                EmojiBottomSheetDialog.newInstance(click.messageId).show(supportFragmentManager, null)
            }
            is TypeClick.OpenActionDialog -> {
                ActionDialog.newInstance(click.messageId).show(supportFragmentManager, null)
            }
            else -> {
                Log.d(javaClass.simpleName, "unknown type click")
            }
        }
    }

    override fun addReaction(messageId: Int, emojiName: String, position: Int) {
        presenter.obtainEvent(
            Event.EventChat.AddReaction(
                messageId = messageId,
                emojiName = emojiName,
                streamTitle = streamTitle,
                topicTitle = topicTitle
            )
        )
    }

    override fun deleteReaction(messageId: Int, emojiName: String, position: Int) {
        presenter.obtainEvent(
            Event.EventChat.DeleteReaction(
                messageId = messageId,
                emojiName = emojiName,
                streamTitle = streamTitle,
                topicTitle = topicTitle
            )
        )
    }

    override fun render(state: State<List<ItemFingerPrint>>) {
        when (state) {
            is State.Result -> {
                adapter.messages = state.data

                if (positionRecyclerView == TypeScroll.DOWN) {
                    rvChat.smoothScrollToPosition(state.data.size)
                }
                isLoadNewPage = true
            }
            is State.Loading -> {

//                rvChat.showShimmer()
            }
            is State.Error -> {
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initViews() {
        rvChat = findViewById(R.id.rv_chat)

        btnSend = findViewById(R.id.btn_send)
        inputMessage = findViewById(R.id.input_message)
        btnAddFile = findViewById(R.id.btn_add_file)
        toolBarChat = findViewById(R.id.toolbar_chat)
        tvTopic = findViewById(R.id.tv_topic)
    }

    private fun setupText() {
        toolBarChat.title = this.getString(R.string.title_topic_percent, streamTitle)
        tvTopic.text = this.getString(R.string.title_topic, topicTitle)
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(this)
        rvChat.adapter = adapter

        rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position: Int =
                        (rvChat.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    positionRecyclerView = TypeScroll.TOP

                    if (position <= REMAINDER && isLoadNewPage) {
                        val messageId =
                            adapter.messages.first { uiItem -> uiItem is MessageFingerPrint }.id.toLong()

                        presenter.obtainEvent(
                            Event.EventChat.LoadNextMessages(
                                streamTitle = streamTitle,
                                topicTitle = topicTitle,
                                anchor = messageId
                            )
                        )

                        isLoadNewPage = false
                    }
                }
            }
        })
    }

    private fun setClickListener() {
        btnSend.setOnClickListener {
            presenter.obtainEvent(
                Event.EventChat.SendMessage(
                    streamTitle = streamTitle,
                    streamId = streamId,
                    topicTitle = topicTitle,
                    message = inputMessage.text.toString()
                )
            )
            positionRecyclerView = TypeScroll.DOWN
            inputMessage.setText(EMPTY_STRING)
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

        private const val REMAINDER = 5
        private const val ARG_TITLE_STREAM = "title_stream"
        private const val ARG_TITLE_TOPIC = "title_topic"
        private const val ARG_ID_TOPIC = "id_topic"
        private const val ARG_STREAM_ID = "id_stream"
        private const val EMPTY_STRING = ""

        fun createIntent(context: Context, item: TopicFingerPrint): Intent {
            return Intent(context, ChatActivity::class.java)
                .putExtra(ARG_TITLE_STREAM, item.streamTitle)
                .putExtra(ARG_ID_TOPIC, item.topic.id)
                .putExtra(ARG_STREAM_ID, item.streamId)
                .putExtra(ARG_TITLE_TOPIC, item.topic.title)

        }
    }
}