package com.baiganov.fintech.presentation.ui.channels.streams

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.channels.SearchQueryListener
import com.baiganov.fintech.presentation.ui.channels.streams.StreamsPresenter.Companion.INITIAL_QUERY
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.ExpandableAdapter
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.StreamFingerPrint
import com.baiganov.fintech.presentation.model.TopicFingerPrint
import com.baiganov.fintech.presentation.ui.chat.ChatActivity
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import com.facebook.shimmer.ShimmerFrameLayout
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class StreamsFragment : MvpAppCompatFragment(), StreamsView, ItemClickListener,
    SearchQueryListener {

    private lateinit var rvStreams: RecyclerView
    private lateinit var frameNotResult: LinearLayout
    private lateinit var adapterStreams: ExpandableAdapter
    private lateinit var shimmer: ShimmerFrameLayout

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
        shimmer = view.findViewById(R.id.shimmer_streams)
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

                shimmer.isVisible = false
            }
            is State.Loading -> {
                frameNotResult.isVisible = false
                if (adapterStreams.itemCount == 0) {
                    shimmer.isVisible = true
                }
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                frameNotResult.isVisible = false
                shimmer.isVisible = false
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