package com.baiganov.fintech.presentation.ui.channels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.baiganov.fintech.presentation.ui.channels.streams.StreamsFragment

class ChannelsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return ChannelsPages.values().size
    }

    override fun createFragment(position: Int): Fragment {
        return StreamsFragment.newInstance(position)
    }
}

enum class ChannelsPages {
    SUBSCRIBED,
    ALL_STREAMS
}