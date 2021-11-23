package com.baiganov.fintech.ui.chat

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.MessageRepositoryImpl
import com.baiganov.fintech.data.db.DatabaseModule
import com.baiganov.fintech.data.db.MessagesDao
import com.baiganov.fintech.data.network.NetworkModule
import com.baiganov.fintech.ui.Event
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.ui.chat.dialog.ActionDialog
import com.baiganov.fintech.ui.chat.recyclerview.MessageAdapter
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.util.State
import com.baiganov.fintech.сustomview.OnClickMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatActivity : AppCompatActivity(), OnClickMessage, OnResultListener {

    private lateinit var adapter: MessageAdapter
    private lateinit var toolBarChat: Toolbar
    private lateinit var tvTopic: TextView
    private lateinit var btnSend: FloatingActionButton
    private lateinit var inputMessage: EditText
    private lateinit var btnAddFile: ImageButton
    private lateinit var rvChat: RecyclerView
    private lateinit var viewModel: ChatViewModel

    private val streamTitle: String by lazy { intent.extras?.getString(ARG_TITLE_STREAM)!! }
    private val topicTitle: String by lazy { intent.extras?.getString(ARG_TITLE_TOPIC)!! }
    private val streamId: Int by lazy { intent.extras?.getInt(ARG_STREAM_ID)!! }

    private var positionRecyclerView: TypeScroll = TypeScroll.DOWN
    private var isLoadNewPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        setupText()
        setupRecyclerView()
        setupViewModel()

        viewModel.messages.observe(this) { handleState(it) }
        viewModel.obtainEvent(
            Event.EventChat.LoadFirstMessages(
                streamTitle = streamTitle,
                topicTitle = topicTitle,
                streamId = streamId
            )
        )

        setClickListener()
    }

    override fun sendData(action: TypeClick) {
        when (action) {
            is TypeClick.AddReaction -> {
                action.messageId?.let {
                    viewModel.obtainEvent(
                        Event.EventChat.AddReaction(
                            messageId = action.messageId,
                            emojiName = action.emoji,
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
                viewModel.obtainEvent(
                    Event.EventChat.DeleteMessage(
                        streamTitle = streamTitle,
                        topicTitle = topicTitle,
                        messageId = action.messageId,
                    )
                )
                Toast.makeText(this, "deleted message", Toast.LENGTH_SHORT).show()
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
        viewModel.obtainEvent(
            Event.EventChat.AddReaction(
                messageId = messageId,
                emojiName = emojiName,
                streamTitle = streamTitle,
                topicTitle = topicTitle
            )
        )
    }

    override fun deleteReaction(messageId: Int, emojiName: String, position: Int) {
        viewModel.obtainEvent(
            Event.EventChat.DeleteReaction(
                messageId = messageId,
                emojiName = emojiName,
                streamTitle = streamTitle,
                topicTitle = topicTitle
            )
        )
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
                            (adapter.messages.first() as MessageFingerPrint).message.id.toLong()

                        viewModel.obtainEvent(
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
            viewModel.obtainEvent(
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

    private fun handleState(it: State<List<ItemFingerPrint>>) {
        when (it) {
            is State.Result -> {
                adapter.messages = it.data

                if (positionRecyclerView == TypeScroll.DOWN) {
                    rvChat.smoothScrollToPosition(adapter.messages.size)
                }

//                rvChat.hideShimmer()
                isLoadNewPage = true
            }
            is State.Loading -> {

//                rvChat.showShimmer()
            }
            is State.Error -> {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//                rvChat.hideShimmer()
            }
        }
    }

    private fun setupViewModel() {
        val networkModule = NetworkModule()
        val service = networkModule.create()

        val databaseModule = DatabaseModule()
        val messagesDao: MessagesDao = databaseModule.create(this).messagesDao()

        val viewModelFactory =
            ChatViewModelFactory(MessageRepositoryImpl(service = service, messagesDao = messagesDao))

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ChatViewModel::class.java)
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