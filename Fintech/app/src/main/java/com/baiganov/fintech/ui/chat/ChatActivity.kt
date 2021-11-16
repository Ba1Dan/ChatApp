package com.baiganov.fintech.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher import android.view.View
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
import com.baiganov.fintech.data.MessageRepository
import com.baiganov.fintech.data.db.DatabaseModule
import com.baiganov.fintech.data.db.MessagesDao
import com.baiganov.fintech.data.network.NetworkModule
import com.baiganov.fintech.util.State
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.ui.chat.recyclerview.MessageAdapter
import com.baiganov.fintech.ui.chat.recyclerview.MessageDiffUtil
import com.baiganov.fintech.ui.chat.recyclerview.MessageFingerPrint
import com.baiganov.fintech.сustomview.OnClickMessage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todkars.shimmer.ShimmerRecyclerView


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

    private var isLoadNewPage = true
    private var positionRecyclerView: TypeScroll = TypeScroll.DOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        setupText(streamTitle, topicTitle)
        setupRecyclerView()
        setupViewModel()

        viewModel.messages.observe(this) { handleState(it) }
        viewModel.getMessagesFromDb(topicTitle, streamId)
        viewModel.loadMessage(streamTitle, topicTitle, streamId)

        setClickListener(streamId, topicTitle, streamTitle)
    }

    override fun sendData(messageId: Int?, emoji: String) {
        messageId?.let {
            viewModel.addReaction(messageId, emoji, streamTitle, topicTitle)
        }
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        EmojiBottomSheetDialog.newInstance(position).show(supportFragmentManager, null)
    }

    override fun addReaction(idMessage: Int, nameEmoji: String, position: Int) {
        viewModel.addReaction(idMessage, nameEmoji, streamTitle, topicTitle)
    }

    override fun deleteReaction(idMessage: Int, nameEmoji: String, position: Int) {
        viewModel.deleteReaction(idMessage, nameEmoji, streamTitle, topicTitle)
    }

    private fun initViews() {
        rvChat = findViewById(R.id.rv_chat)

        btnSend = findViewById(R.id.btn_send)
        inputMessage = findViewById(R.id.input_message)
        btnAddFile = findViewById(R.id.btn_add_file)
        toolBarChat = findViewById(R.id.toolbar_chat)
        tvTopic = findViewById(R.id.tv_topic)
    }

    private fun setupText(titleStream: String, titleTopic: String) {
        toolBarChat.title = titleStream
        tvTopic.text = this.getString(R.string.title_topic, titleTopic)
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(this)
        rvChat.adapter = adapter

        rvChat.addOnScrollListener( object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position: Int = (rvChat.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    positionRecyclerView = TypeScroll.TOP

                    if (position <= REMAINDER && isLoadNewPage) {
                        val messageId = (adapter.messages.first() as MessageFingerPrint).message.id.toLong()

                        viewModel.loadNextMessages(streamTitle, topicTitle,  streamId, messageId)

                        isLoadNewPage = false
                    }
                }
            }
        })
    }

    private fun setClickListener(streamId: Int, titleTopic: String, titleStream: String) {
        btnSend.setOnClickListener {
            viewModel.sendMessage(titleStream, streamId, titleTopic, inputMessage.text.toString())

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
                    (rvChat.layoutManager as LinearLayoutManager).stackFromEnd = true
                    rvChat.smoothScrollToPosition(adapter.messages.size)
                } else {
                    (rvChat.layoutManager as LinearLayoutManager).stackFromEnd = false
                }

//                rvChat.hideShimmer()

                isLoadNewPage = true
            }
            is State.Loading -> {
                if (adapter.itemCount == 0) {
//                    rvChat.showShimmer()
                }
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

        val viewModelFactory = ChatViewModelFactory(MessageRepository(service = service, messagesDao = messagesDao))

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