package com.baiganov.fintech.presentation.ui.channels.streams

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.channels.SearchQueryListener
import com.baiganov.fintech.presentation.ui.channels.streams.StreamsPresenter.Companion.INITIAL_QUERY
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.ExpandableAdapter
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.StreamFingerPrint
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.chat.ChatActivity
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import com.todkars.shimmer.ShimmerRecyclerView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class StreamsFragment : MvpAppCompatFragment(), StreamsView, ItemClickListener,
    SearchQueryListener {

    private lateinit var rvStreams: ShimmerRecyclerView
    private lateinit var frameNotResult: LinearLayout
    private lateinit var adapterStreams: ExpandableAdapter

    @Inject
    lateinit var presenterProvider: Provider<StreamsPresenter>

    private val presenter: StreamsPresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

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
        presenter.tabPosition = requireArguments().getInt(ARG_TAB_POSITION)


        presenter.obtainEvent(
            Event.EventChannels.SearchStreams(
                INITIAL_QUERY
            )
        )

        adapterStreams = ExpandableAdapter(this)
        rvStreams.adapter = adapterStreams
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        when (item) {
            is StreamFingerPrint -> {
                if (item.isExpanded) {
                    presenter.obtainEvent(
                        Event.EventChannels.OpenStream(
                            position,
                            item.childTopics
                        )
                    )
                } else {
                    presenter.obtainEvent(
                        Event.EventChannels.CloseStream(
                            item.childTopics
                        )
                    )
                }
            }
            is TopicFingerPrint -> {
                startActivity(ChatActivity.createIntent(requireContext(), item))
            }
        }
    }

    override fun render(state: State<List<ItemFingerPrint>>) {
        when (state) {
            is State.Result -> {
                adapterStreams.dataOfList = state.data

                frameNotResult.isVisible = state.data.isEmpty()

                rvStreams.hideShimmer()
            }
            is State.Loading -> {
                frameNotResult.isVisible = false
                if (adapterStreams.itemCount == 0) {
                    rvStreams.showShimmer()
                }
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                frameNotResult.isVisible = false
                rvStreams.hideShimmer()
            }
        }
    }

    override fun search(searchQuery: String) {
        presenter.obtainEvent(
            Event.EventChannels.SearchStreams(
                searchQuery
            )
        )
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