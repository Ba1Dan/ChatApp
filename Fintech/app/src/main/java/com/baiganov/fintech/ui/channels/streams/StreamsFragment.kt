package com.baiganov.fintech.ui.channels.streams

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
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener


class StreamsFragment : Fragment(), ItemClickListener {

    private lateinit var rvStreams: RecyclerView
    private lateinit var adapterStreams: ExpandableAdapter
    private lateinit var dataManager: DataManager

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
        adapterStreams = ExpandableAdapter(this)
        rvStreams.adapter = adapterStreams
        dataManager = DataManager()
        adapterStreams.dataOfList = dataManager.data
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        when(item) {
            is StreamFingerPrint -> {
                if (item.isExpanded) {
                    adapterStreams.dataOfList = dataManager.add(position, item.childTopics)
                } else {
                    adapterStreams.dataOfList = dataManager.remove(position, item.childTopics)
                }
            }
            is TopicFingerPrint -> {
                Toast.makeText(requireContext(), "$position ${item.topic.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            StreamsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}