package com.baiganov.fintech.presentation.ui.channels.streams

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

class CreateStreamDialog : DialogFragment() {

    private lateinit var inputName: EditText
    private lateinit var inputDescription: EditText
    private lateinit var btnCreate: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_stream_dialog, container, false)

        inputName = view.findViewById(R.id.input_name)
        inputDescription = view.findViewById(R.id.input_description)
        btnCreate = view.findViewById(R.id.btn_create_stream)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCreate.setOnClickListener {
            setFragmentResult(
                CREATE_STREAM_REQUEST_KEY,
                bundleOf(
                    NAME_RESULT_KEY to inputName.text.toString(),
                    DESCRIPTION_RESULT_KEY to inputDescription.text.toString(),
                )
            )
            dismiss()
        }
    }

    override fun getTheme() = R.style.RoundedCornersDialog

    companion object {

        const val CREATE_STREAM_REQUEST_KEY = "create_stream_request"
        const val NAME_RESULT_KEY = "name_result"
        const val DESCRIPTION_RESULT_KEY = "description_result"

        fun newInstance() = CreateStreamDialog()
    }
}