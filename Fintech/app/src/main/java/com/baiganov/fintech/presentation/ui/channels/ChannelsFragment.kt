package com.baiganov.fintech.presentation.ui.channels

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.viewpager2.widget.ViewPager2
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.util.Event
import com.baiganov.fintech.util.State
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class ChannelsFragment : MvpAppCompatFragment(), ChannelsView {

    @Inject
    lateinit var presenterProvider: Provider<ChannelsPresenter>

    private val presenter: ChannelsPresenter by moxyPresenter { presenterProvider.get() }

    private lateinit var viewPagerChannels: ViewPager2
    private lateinit var tabLayoutChannels: TabLayout
    private lateinit var searchView: SearchView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_channels, container, false)

        viewPagerChannels = view.findViewById(R.id.view_pager_channels)
        tabLayoutChannels = view.findViewById(R.id.tab_layout_channels)
        searchView = view.findViewById(R.id.search)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerChannels.apply {
            adapter = ChannelsViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            offscreenPageLimit = ChannelsPages.values().size - 1
        }

        TabLayoutMediator(tabLayoutChannels, viewPagerChannels) { tab, position ->
            tab.text = getString(
                when (position) {
                    ChannelsPages.SUBSCRIBED.ordinal -> R.string.tab_title_subscribed_streams
                    ChannelsPages.ALL_STREAMS.ordinal -> R.string.tab_title_all_streams
                    else -> throw IllegalStateException("Undefined tab position: $position")
                }
            )
        }.attach()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                presenter.obtainEvent(Event.EventChannels.SearchStreams(p0.orEmpty()))
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                presenter.obtainEvent(Event.EventChannels.SearchStreams(p0.orEmpty()))
                return true
            }
        })
    }

    override fun render(state: State<String>) {
        when (state) {
            is State.Result -> {
                val fragment = childFragmentManager.findFragmentByTag("f${viewPagerChannels.currentItem}")

                fragment?.let {
                    (it as SearchQueryListener).search(state.data)
                }
            }
        }
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}