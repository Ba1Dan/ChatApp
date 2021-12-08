package com.baiganov.fintech.presentation.ui.chat.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.TypeClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ActionDialog : BottomSheetDialogFragment() {

    private lateinit var btnEdit: TextView
    private lateinit var btnDelete: TextView
    private lateinit var btnAddReaction: TextView
    private lateinit var onResultListener: OnResultListener

    private val messageId: Int by lazy { arguments?.getInt(ARGUMENT_MESSAGE_ID)!! }

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

        btnEdit = view.findViewById(R.id.btn_edit)
        btnDelete = view.findViewById(R.id.btn_delete)
        btnAddReaction = view.findViewById(R.id.btn_add_reaction)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnAddReaction.setOnClickListener {
            EmojiBottomSheetDialog.newInstance(messageId).show(parentFragmentManager, null)
            dismiss()
        }
        btnEdit.setOnClickListener {
            onResultListener.sendData(TypeClick.EditMessage(messageId))
            dismiss()
        }
        btnDelete.setOnClickListener {
            onResultListener.sendData(TypeClick.DeleteMessage(messageId))
            dismiss()
        }
    }

    companion object {

        private const val ARGUMENT_MESSAGE_ID = "message_id"

        fun newInstance(messageId: Int) = ActionDialog().apply {
            val bundle = Bundle()
            bundle.putInt(ARGUMENT_MESSAGE_ID, messageId)
            arguments = bundle
        }
    }
}