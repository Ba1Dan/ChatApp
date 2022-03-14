package com.baiganov.fintech.presentation.ui.channels

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.databinding.FragmentChannelsBinding
import com.baiganov.fintech.presentation.ViewModelFactory
import com.baiganov.fintech.presentation.ui.base.BaseFragment
import com.baiganov.fintech.presentation.util.State
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class ChannelsFragment : BaseFragment<FragmentChannelsBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ChannelsViewModel

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChannelsBinding {
        return FragmentChannelsBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[ChannelsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPagerChannels.apply {
            adapter = ChannelsViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            offscreenPageLimit = ChannelsPages.values().size - 1
        }

        TabLayoutMediator(binding.tabLayoutChannels, binding.viewPagerChannels) { tab, position ->
            tab.text = getString(
                when (position) {
                    ChannelsPages.SUBSCRIBED.ordinal -> R.string.tab_title_subscribed_streams
                    ChannelsPages.ALL_STREAMS.ordinal -> R.string.tab_title_all_streams
                    else -> throw IllegalStateException("Undefined tab position: $position")
                }
            )
        }.attach()

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.searchTopics(p0.orEmpty())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.searchTopics(p0.orEmpty())
                return true
            }
        })

        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: State<String>) {
        when (state) {
            is State.Result -> {
                val fragment = childFragmentManager.findFragmentByTag("f${binding.viewPagerChannels.currentItem}")

                fragment?.let {
                    (it as SearchQueryListener).search(state.data)
                }
            }
            else -> {
                Log.d(javaClass.simpleName, "unknown state")
            }
        }
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}