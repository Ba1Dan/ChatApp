package com.baiganov.fintech.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.channels.ChannelsFragment
import com.baiganov.fintech.ui.people.PeopleFragment
import com.baiganov.fintech.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var channelsFragment: ChannelsFragment
    private lateinit var peopleFragment: PeopleFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channelsFragment = ChannelsFragment.newInstance()
        peopleFragment = PeopleFragment.newInstance()
        profileFragment = ProfileFragment.newInstance()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        supportFragmentManager.commit {
            add(R.id.main_fragment_container, channelsFragment, BottomNavigationPages.CHANNELS.name)
            add(R.id.main_fragment_container, peopleFragment, BottomNavigationPages.CHANNELS.name)
            add(R.id.main_fragment_container, profileFragment, BottomNavigationPages.CHANNELS.name)
        }

        setFragment(BottomNavigationPages.CHANNELS)

        bottomNavigationView.setOnItemSelectedListener  { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> setFragment(BottomNavigationPages.CHANNELS)
                R.id.people_menu_item -> setFragment(BottomNavigationPages.PEOPLE)
                R.id.profile_menu_item -> setFragment(BottomNavigationPages.PROFILE)
                else -> false
            }
        }
    }

    private fun setFragment(page: BottomNavigationPages): Boolean {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        when (page) {
            BottomNavigationPages.CHANNELS -> {
                transaction.show(channelsFragment)
                transaction.hide(profileFragment)
                transaction.hide(peopleFragment)
            }
            BottomNavigationPages.PEOPLE -> {
                transaction.hide(channelsFragment)
                transaction.hide(profileFragment)
                transaction.show(peopleFragment)
            }
            BottomNavigationPages.PROFILE -> {
                transaction.hide(channelsFragment)
                transaction.show(profileFragment)
                transaction.hide(peopleFragment)
            }
        }
        transaction.commit()
        return true
    }
}