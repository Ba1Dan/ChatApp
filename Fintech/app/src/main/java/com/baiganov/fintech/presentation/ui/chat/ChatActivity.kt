package com.baiganov.fintech.presentation.ui.chat

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.StreamEntity
import com.baiganov.fintech.databinding.ActivityChatBinding
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.ViewModelFactory
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.MessageFingerPrint
import com.baiganov.fintech.presentation.model.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.base.BaseActivity
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.ActionDialog
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog.Companion.MESSAGE_ID_RESULT_KEY
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog.Companion.NEW_CONTENT_RESULT_KEY
import com.baiganov.fintech.presentation.ui.chat.dialog.EditMessageDialog.Companion.REQUEST_KEY_EDIT_MESSAGE
import com.baiganov.fintech.presentation.ui.chat.dialog.EditTopicDialog
import com.baiganov.fintech.presentation.ui.chat.dialog.EditTopicDialog.Companion.NEW_TOPIC_RESULT_KEY
import com.baiganov.fintech.presentation.ui.chat.dialog.EditTopicDialog.Companion.REQUEST_KEY_EDIT_TOPIC
import com.baiganov.fintech.presentation.ui.chat.recyclerview.MessageAdapter
import com.baiganov.fintech.presentation.view.OnClickMessage
import com.baiganov.fintech.presentation.util.State
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ChatActivity : BaseActivity<ActivityChatBinding>(), OnClickMessage, OnResultListener {

    @Inject
    lateinit var networkManager: NetworkManager

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapter: MessageAdapter
    private lateinit var viewModel: ChatViewModel

    private val component by lazy { (application as App).component }
    private val streamTitle: String by lazy { intent.extras?.getString(ARG_TITLE_STREAM)!! }
    private val topicTitle: String? by lazy { intent.extras?.getString(ARG_TITLE_TOPIC) }
    private val streamId: Int by lazy { intent.extras?.getInt(ARG_STREAM_ID)!! }

    private var positionRecyclerView: TypeScroll = TypeScroll.DOWN
    private var isLoadNewPage = true

    override fun getBinding(inflater: LayoutInflater): ActivityChatBinding {
        return ActivityChatBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        component.inject(this)
        networkManager.registerCallback()
        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
        setupText()
        setupRecyclerView()
        viewModel.init(streamTitle, streamId, topicTitle)

        networkManager.isConnectedNetwork.observe(this) { isNetwork ->
            binding.notification.root.isVisible = !isNetwork
            viewModel.isConnected = isNetwork
        }

        viewModel.loadMessage(streamId = streamId)

        viewModel.state.observe(this) { state ->
            render(state)
        }
        setClickListener()
        setFragmentResultListener()
    }

    override fun sendData(click: TypeClick) {
        when (click) {
            is TypeClick.AddReaction -> {
                click.messageId?.let {
                    addReaction(click.messageId, click.emoji)
                }
            }
            is TypeClick.EditMessage -> {
                EditMessageDialog.newInstance(click.message).show(supportFragmentManager, null)
            }
            is TypeClick.DeleteMessage -> {
                viewModel.deleteMessage(streamId, click.messageId)
                Toast.makeText(this, getString(R.string.deleted_message), Toast.LENGTH_SHORT).show()
            }
            is TypeClick.Copy -> {
                copyMessage(click.message.content)
            }
            is TypeClick.EditTopic -> {
                EditTopicDialog.newInstance(click.messageId, click.topicName)
                    .show(supportFragmentManager, null)
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

    override fun addReaction(messageId: Int, emojiName: String) {
        viewModel.addReaction(messageId, emojiName)
    }

    override fun deleteReaction(messageId: Int, emojiName: String) {
        viewModel.deleteReaction(messageId, emojiName)
    }

    private fun render(state: State<List<ItemFingerPrint>>) {
        when (state) {
            is State.Result -> {
                binding.shimmerMessages.root.isVisible = false

                adapter.messages = state.data

                if (positionRecyclerView == TypeScroll.DOWN) {
                    binding.rvChat.smoothScrollToPosition(state.data.size)
                }
                isLoadNewPage = true
            }

            is State.Loading -> {
                binding.shimmerMessages.root.isVisible = true
            }

            is State.Error -> {
                binding.shimmerMessages.root.isVisible = false
                Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
            }

            is State.AddFile -> {
                binding.pgFile.isVisible = false
                binding.inputMessage.append(state.uri)
            }

            is State.LoadingFile -> {

                binding.pgFile.isVisible = true
            }
        }
    }

    private fun setupText() {
        binding.toolbarChat.title = this.getString(R.string.title_topic_percent, streamTitle)
        binding.tvTopic.text = this.getString(R.string.title_topic, topicTitle)
    }

    private fun setupRecyclerView() {
        (binding.rvChat.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        adapter = MessageAdapter(this)
        binding.rvChat.adapter = adapter

        binding.rvChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val position: Int =
                        (binding.rvChat.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    positionRecyclerView = TypeScroll.TOP

                    if (position <= REMAINDER && isLoadNewPage) {
                        val messageId =
                            adapter.messages.first { uiItem -> uiItem is MessageFingerPrint }.id.toLong()

                        viewModel.loadNextMessages(
                            anchor = messageId
                        )
                        isLoadNewPage = false
                    }
                }
            }
        })
    }

    private fun setClickListener() {
        binding.btnSend.setOnClickListener {
            viewModel.sendMessage(
                streamId,
                binding.inputMessage.text.toString(),
                binding.inputTopic.text.toString(),
            )
            positionRecyclerView = TypeScroll.DOWN
            binding.inputMessage.setText(EMPTY_STRING)
        }

        binding.inputMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.toString().isEmpty()) {
                        binding.btnAddFile.visibility = View.VISIBLE
                        binding.btnSend.visibility = View.GONE
                    } else {
                        binding.btnSend.visibility = View.VISIBLE
                        binding.btnAddFile.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.toolbarChat.setNavigationOnClickListener { finish() }

        val getContentLauncher = registerUploadFileActivityLauncher()
        binding.btnAddFile.setOnClickListener {
            getContentLauncher.launch(ALL_TYPE)
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
                Toast.makeText(this, getString(R.string.toast_file), Toast.LENGTH_SHORT).show()
            } else if (type != null) {
                viewModel.uploadFile(uri, type, name)
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

    private fun copyMessage(message: String) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(null, message)
        clipBoard.setPrimaryClip(clip)

        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
    }

    private fun setFragmentResultListener() {
        supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY_EDIT_MESSAGE,
            this
        ) { _, bundle ->
            val id: Int = bundle.getInt(MESSAGE_ID_RESULT_KEY)
            val content: String = bundle.getString(NEW_CONTENT_RESULT_KEY).orEmpty()

            viewModel.editMessage(id, content)
        }

        supportFragmentManager.setFragmentResultListener(
            REQUEST_KEY_EDIT_TOPIC,
            this
        ) { _, bundle ->

            val id: Int = bundle.getInt(MESSAGE_ID_RESULT_KEY)
            val topic: String = bundle.getString(NEW_TOPIC_RESULT_KEY).orEmpty()

            viewModel.editTopic(id, topic)
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
        private const val ALL_TYPE = "*/*"

        fun createIntent(context: Context, item: TopicFingerPrint): Intent {
            return Intent(context, ChatActivity::class.java)
                .putExtra(ARG_TITLE_STREAM, item.streamTitle)
                .putExtra(ARG_ID_TOPIC, item.topic.id)
                .putExtra(ARG_STREAM_ID, item.streamId)
                .putExtra(ARG_TITLE_TOPIC, item.topic.title)
        }

        fun createIntent(context: Context, stream: StreamEntity): Intent {
            return Intent(context, ChatActivity::class.java)
                .putExtra(ARG_TITLE_STREAM, stream.name)
                .putExtra(ARG_STREAM_ID, stream.streamId)
        }
    }
}