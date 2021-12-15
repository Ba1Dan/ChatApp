package com.baiganov.fintech.presentation.ui.chat.bottomsheet

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionDialog : BottomSheetDialogFragment() {

    private lateinit var btnEditMessage: TextView
    private lateinit var btnDelete: TextView
    private lateinit var btnAddReaction: TextView
    private lateinit var btnCopy: TextView
    private lateinit var btnEditTopic: TextView
    private lateinit var onResultListener: OnResultListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onResultListener = context as OnResultListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.action_dialog, container, false)

        btnEditMessage = view.findViewById(R.id.btn_edit)
        btnDelete = view.findViewById(R.id.btn_delete)
        btnAddReaction = view.findViewById(R.id.btn_add_reaction)
        btnCopy = view.findViewById(R.id.btn_copy)
        btnEditTopic = view.findViewById(R.id.btn_edit_topic)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message: MessageEntity = requireArguments().getParcelable<MessageEntity>(ARGUMENT_MESSAGE) as MessageEntity

        btnAddReaction.setOnClickListener {
            EmojiBottomSheetDialog.newInstance(message.id).show(parentFragmentManager, null)
            dismiss()
        }
        btnEditMessage.setOnClickListener {
            onResultListener.sendData(TypeClick.EditMessage(message))
            dismiss()
        }
        btnDelete.setOnClickListener {
            onResultListener.sendData(TypeClick.DeleteMessage(message.id))
            dismiss()
        }
        btnCopy.setOnClickListener {
            onResultListener.sendData(TypeClick.Copy(message))
            dismiss()
        }
        btnEditTopic.setOnClickListener {
            Log.d("xxxxx", "click dialog")
            onResultListener.sendData(TypeClick.EditTopic(message.id, message.topicName))
            dismiss()
        }
    }

    companion object {

        private const val ARGUMENT_MESSAGE = "message"

        fun newInstance(message: MessageEntity) = ActionDialog().apply {
            val bundle = Bundle()
            bundle.putParcelable(ARGUMENT_MESSAGE, message)
            arguments = bundle
        }
    }
}