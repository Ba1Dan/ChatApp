package com.baiganov.fintech.presentation.ui.chat

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.ActionDialog
import com.baiganov.fintech.presentation.ui.chat.recyclerview.MessageAdapter
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog.Companion.MESSAGE_ID_RESULT_KEY
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog.Companion.NEW_CONTENT_RESULT_KEY
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog.Companion.REQUEST_KEY_EDIT_MESSAGE
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import com.baiganov.fintech.presentation.сustomview.OnClickMessage
import com.facebook.shimmer.ShimmerFrameLayout
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
    private lateinit var shimmer: ShimmerFrameLayout

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
        presenter.init(streamTitle, topicTitle)

        presenter.obtainEvent(
            Event.EventChat.LoadFirstMessages(
                streamTitle = streamTitle,
                topicTitle = topicTitle,
                streamId = streamId
            )
        )

        setClickListener()

        supportFragmentManager
            .setFragmentResultListener(REQUEST_KEY_EDIT_MESSAGE, this) { requestKey, bundle ->
                // We use a String here, but any type that can be put in a Bundle is supported
                Log.d("edit_message", "getting content")
                val id: Int = bundle.getInt(MESSAGE_ID_RESULT_KEY)
                val content: String = bundle.getString(NEW_CONTENT_RESULT_KEY).orEmpty()

                presenter.editMessage(id, content)
                // Do something with the result
            }
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

                EditMessageDialog.newInstance(click.message).show(supportFragmentManager, null)
            }

            is TypeClick.DeleteMessage -> {
                presenter.obtainEvent(
                    Event.EventChat.DeleteMessage(
                        streamTitle = streamTitle,
                        topicTitle = topicTitle,
                        streamId = streamId,
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
        when (click) {
            is TypeClick.OpenBottomSheet -> {
                EmojiBottomSheetDialog.newInstance(click.messageId)
                    .show(supportFragmentManager, null)
            }
            is TypeClick.OpenActionDialog -> {
                ActionDialog.newInstance(click.message).show(supportFragmentManager, null)
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
                shimmer.isVisible = false
                adapter.messages = state.data

                if (positionRecyclerView == TypeScroll.DOWN) {
                    rvChat.smoothScrollToPosition(state.data.size)
                }
                isLoadNewPage = true
            }

            is State.Loading -> {
                shimmer.isVisible = true
            }

            is State.Error -> {
                shimmer.isVisible = false
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
        shimmer = findViewById(R.id.shimmer_messages)
    }

    private fun setupText() {
        toolBarChat.title = this.getString(R.string.title_topic_percent, streamTitle)
        tvTopic.text = this.getString(R.string.title_topic, topicTitle)
    }

    private fun setupRecyclerView() {
        (rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
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

        val getContentLauncher = registerUploadFileActivityLauncher()
        btnAddFile.setOnClickListener {
            getContentLauncher.launch("*/*")
        }
    }

    private fun registerUploadFileActivityLauncher(): ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { data ->
            data?.let { uri ->
                uploadFile(uri)
            }
        }

    private fun uploadFile(uri: Uri) {
        val contentResolver = this.contentResolver
        val type = contentResolver.getType(uri)

        getNameAndSize(contentResolver, uri) { name: String, size: Int ->
            if (size > MEGABYTES_25_IN_BYTES) {
                Toast.makeText(this, "Big file", Toast.LENGTH_SHORT).show()
            } else if (type != null) {
                presenter.obtainEvent(
                    Event.EventChat.UploadFile(
                        name = name,
                        uri = uri,
                        type = type
                    )
                )
            }
        }
    }

    private fun getNameAndSize(
        contentResolver: ContentResolver,
        uri: Uri,
        callback: (String, Int) -> Unit
    ) {
        contentResolver.query(uri, null, null, null, null)
            ?.use { cursor ->
                val nameColumn = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val sizeColumn = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)

                cursor.moveToFirst()
                val name = cursor.getString(nameColumn)
                val size = cursor.getInt(sizeColumn)

                callback(name, size)
            }

    }

    companion object {

        private const val REMAINDER = 5
        private const val ARG_TITLE_STREAM = "title_stream"
        private const val ARG_TITLE_TOPIC = "title_topic"
        private const val ARG_ID_TOPIC = "id_topic"
        private const val ARG_STREAM_ID = "id_stream"
        private const val EMPTY_STRING = ""
        private const val MEGABYTES_25_IN_BYTES = 26_214_400

        fun createIntent(context: Context, item: TopicFingerPrint): Intent {
            return Intent(context, ChatActivity::class.java)
                .putExtra(ARG_TITLE_STREAM, item.streamTitle)
                .putExtra(ARG_ID_TOPIC, item.topic.id)
                .putExtra(ARG_STREAM_ID, item.streamId)
                .putExtra(ARG_TITLE_TOPIC, item.topic.title)

        }
    }
}