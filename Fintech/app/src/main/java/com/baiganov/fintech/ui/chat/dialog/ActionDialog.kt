package com.baiganov.fintech.ui.chat.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.baiganov.fintech.ui.chat.bottomsheet.OnResultListener
import com.baiganov.fintech.ui.chat.bottomsheet.TypeClick

class ActionDialog : DialogFragment() {

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

        fun newInstance(messageId: Int) = ActionDialog().apply {
            val bundle = Bundle()
            bundle.putInt(ARGUMENT_MESSAGE_ID, messageId)
            arguments = bundle
        }

        private const val ARGUMENT_MESSAGE_ID = "message_id"
    }
}