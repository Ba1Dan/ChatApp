package com.baiganov.fintech.ui.channels.streams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.R
import com.baiganov.fintech.data.ChannelsRepositoryImpl
import com.baiganov.fintech.data.db.DatabaseModule
import com.baiganov.fintech.data.db.StreamsDao
import com.baiganov.fintech.data.network.NetworkModule
import com.baiganov.fintech.ui.channels.ChannelsViewModel
import com.baiganov.fintech.ui.channels.streams.recyclerview.ExpandableAdapter
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.ui.chat.ChatActivity
import com.baiganov.fintech.ui.Event
import com.baiganov.fintech.ui.channels.ChannelsViewModelFactory
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.util.State
import com.todkars.shimmer.ShimmerRecyclerView

class StreamsFragment : Fragment(), ItemClickListener {

    private lateinit var rvStreams: ShimmerRecyclerView
    private lateinit var frameNotResult: LinearLayout
    private lateinit var adapterStreams: ExpandableAdapter
    private var tabPosition: Int = -1
    private lateinit var viewModel: ChannelsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_streams, container, false)
        rvStreams = view.findViewById(R.id.rv_stream)
        frameNotResult = view.findViewById(R.id.no_result_found)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        tabPosition = requireArguments().getInt(ARG_TAB_POSITION)

        viewModel.obtainEvent(Event.EventChannels.SearchStreams(ChannelsViewModel.INITIAL_QUERY, tabPosition))

        adapterStreams = ExpandableAdapter(this)
        rvStreams.adapter = adapterStreams
        initUi()
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        when (item) {
            is StreamFingerPrint -> {
                if (item.isExpanded) {
                    viewModel.obtainEvent(Event.EventChannels.OpenStream(tabPosition, position, item.childTopics))
                } else {
                    viewModel.obtainEvent(Event.EventChannels.CloseStream(tabPosition, item.childTopics))
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
                handleState(it)
            }
        } else {
            viewModel.mainScreenState.observe(viewLifecycleOwner) {
                handleState(it)
            }
        }
    }

    private fun handleState(it: State<List<ItemFingerPrint>>) {
        when (it) {
            is State.Result -> {
                adapterStreams.dataOfList = it.data

                frameNotResult.isVisible = it.data.isEmpty()

                rvStreams.hideShimmer()
            }
            is State.Loading -> {
                frameNotResult.isVisible = false
                if (adapterStreams.itemCount == 0) {
                    rvStreams.showShimmer()
                }
            }
            is State.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                frameNotResult.isVisible = false
                rvStreams.hideShimmer()
            }
        }
    }

    private fun setupViewModel() {
        val networkModule = NetworkModule()
        val service = networkModule.create()

        val databaseModule = DatabaseModule()
        val streamsDao: StreamsDao = databaseModule.create(requireActivity()).streamsDao()

        val viewModelFactory =
            ChannelsViewModelFactory(ChannelsRepositoryImpl(service = service, streamsDao = streamsDao))

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(ChannelsViewModel::class.java)
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