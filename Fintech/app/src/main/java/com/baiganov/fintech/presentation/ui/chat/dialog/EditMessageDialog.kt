package com.baiganov.fintech.presentation.ui.chat.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.baiganov.fintech.R
import com.baiganov.fintech.data.db.entity.MessageEntity
import com.baiganov.fintech.data.model.response.Message
import com.baiganov.fintech.presentation.ui.chat.bottomsheet.ActionDialog

class EditMessageDialog : DialogFragment() {

    private lateinit var editMessage: EditText
    private lateinit var btnEdit: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_message_dialog, container, false)

        editMessage = view.findViewById(R.id.edit_message)
        btnEdit = view.findViewById(R.id.btn_edit)
//        btnEdit = view.findViewById(R.id.btn_edit)
//        btnDelete = view.findViewById(R.id.btn_delete)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val message: MessageEntity = requireArguments().getParcelable<MessageEntity>(ARGUMENT_MESSAGE) as MessageEntity

        editMessage.setText(message.content)

        btnEdit.setOnClickListener {
            setFragmentResult(
                REQUEST_KEY_EDIT_MESSAGE, bundleOf(
                    MESSAGE_ID_RESULT_KEY to message.id,
                    NEW_CONTENT_RESULT_KEY to editMessage.text.toString()
                )
            )
            dismiss()
        }
    }

    companion object {

        private const val ARGUMENT_MESSAGE = "message"
        const val REQUEST_KEY_EDIT_MESSAGE = "message_id"
        const val MESSAGE_ID_RESULT_KEY = "message_id"
        const val NEW_CONTENT_RESULT_KEY = "message_new_content"

        fun newInstance(message: MessageEntity) = EditMessageDialog().apply {
            val bundle = Bundle()
            bundle.putParcelable(ARGUMENT_MESSAGE, message)
            arguments = bundle
        }
    }
}