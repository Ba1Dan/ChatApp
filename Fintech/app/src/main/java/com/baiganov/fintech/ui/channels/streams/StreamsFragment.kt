package com.baiganov.fintech.ui.channels.streams

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.DataManager
import com.baiganov.fintech.ui.channels.streams.recyclerview.ExpandableAdapter
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.ChatActivity
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener


class StreamsFragment : Fragment(), ItemClickListener {

    private lateinit var rvStreams: RecyclerView
    private lateinit var adapterStreams: ExpandableAdapter
    private lateinit var dataManager: DataManager
    private var tabPosition: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_streams, container, false)
        rvStreams = view.findViewById(R.id.rv_stream)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabPosition = requireArguments().getInt(ARG_TAB_POSITION)
        adapterStreams = ExpandableAdapter(this)
        rvStreams.adapter = adapterStreams
        dataManager = DataManager()
        adapterStreams.dataOfList = dataManager.getStreams(tabPosition)
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        when(item) {
            is StreamFingerPrint -> {
                if (item.isExpanded) {
                    adapterStreams.dataOfList = dataManager.add(tabPosition, position, item.childTopics)
                } else {
                    adapterStreams.dataOfList = dataManager.remove(tabPosition, position, item.childTopics)
                }
            }
            is TopicFingerPrint -> {
                val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                    putExtra(ChatActivity.ARG_TITLE_STREAM, item.streamTitle)
                    putExtra(ChatActivity.ARG_ID_TOPIC, item.topic.id)
                    putExtra(ChatActivity.ARG_TITLE_TOPIC, item.topic.title)
                }
                startActivity(intent)

            }
        }
    }

    companion object {

        private const val ARG_TAB_POSITION = "arg_tab_position"
        fun newInstance(tabPosition: Int) =
            StreamsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TAB_POSITION, tabPosition)
                }
            }
    }
}