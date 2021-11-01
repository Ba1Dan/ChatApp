package com.baiganov.fintech.ui.channels.streams

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.MainScreenState
import com.baiganov.fintech.ui.channels.ChannelsViewModel
import com.baiganov.fintech.ui.channels.streams.recyclerview.ExpandableAdapter
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.ChatActivity
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.todkars.shimmer.ShimmerRecyclerView


class StreamsFragment : Fragment(), ItemClickListener {

    private lateinit var rvStreams: ShimmerRecyclerView
    private lateinit var adapterStreams: ExpandableAdapter
    private var tabPosition: Int = -1
    private val viewModel: ChannelsViewModel by activityViewModels()

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
        Log.d("xxx", "tab $tabPosition")
        viewModel.searchTopics(ChannelsViewModel.INITIAL_QUERY, tabPosition)
        adapterStreams = ExpandableAdapter(this)
        rvStreams.adapter = adapterStreams
        initUi()
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        when (item) {
            is StreamFingerPrint -> {
                if (item.isExpanded) {
                    viewModel.openStream(tabPosition, position, item.childTopics)
                } else {
                    viewModel.closeStream(tabPosition, item.childTopics)
                }
            }
            is TopicFingerPrint -> {
                startActivity(ChatActivity.createIntent(requireContext(), item))
            }
        }
    }

    private fun initUi() {
        if (tabPosition == 0) {
            viewModel.subscribeStreams.observe(viewLifecycleOwner) {
                Log.d("xxx", "sub $it")
                processMainScreenState(it)
            }
        } else {
            viewModel.mainScreenState.observe(viewLifecycleOwner) {
                Log.d("xxx", "all $it")
                processMainScreenState(it)
            }
        }
    }

    private fun processMainScreenState(it: MainScreenState?) {
        when (it) {
            is MainScreenState.Result -> {
                adapterStreams.dataOfList = it.items
                rvStreams.hideShimmer()
            }
            MainScreenState.Loading -> {
                rvStreams.showShimmer()
            }
            is MainScreenState.Error -> {
                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
                rvStreams.hideShimmer()
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