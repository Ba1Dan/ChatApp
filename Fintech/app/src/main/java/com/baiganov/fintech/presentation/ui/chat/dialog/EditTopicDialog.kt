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
import com.baiganov.fintech.util.parseHtml

class EditTopicDialog : DialogFragment() {

    private lateinit var editTopic: EditText
    private lateinit var btnEdit: Button

    override fun getTheme() = R.style.RoundedCornersDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_topic_dialog, container, false)

        editTopic = view.findViewById(R.id.input_name)
        btnEdit = view.findViewById(R.id.btn_edit)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val topic: String = requireArguments().getString(ARG_OLD_TOPIC).orEmpty()
        val messageId: Int = requireArguments().getInt(ARG_MESSAGE_ID)

        editTopic.setText(parseHtml(topic))

        btnEdit.setOnClickListener {
            setFragmentResult(
                REQUEST_KEY_EDIT_TOPIC, bundleOf(
                    MESSAGE_ID_RESULT_KEY to messageId,
                    NEW_TOPIC_RESULT_KEY to editTopic.text.toString()
                )
            )
            dismiss()
        }
    }

    companion object {

        private const val ARG_MESSAGE_ID = "message_id"
        private const val ARG_OLD_TOPIC = "old_topic"

        const val REQUEST_KEY_EDIT_TOPIC = "change_topic_request"
        const val MESSAGE_ID_RESULT_KEY = "message_id"
        const val NEW_TOPIC_RESULT_KEY = "new_topic_result"

        fun newInstance(messageId: Int, topicName: String) = EditTopicDialog().apply {
            arguments = bundleOf(
                ARG_MESSAGE_ID to messageId,
                ARG_OLD_TOPIC to topicName
            )
        }
    }
}