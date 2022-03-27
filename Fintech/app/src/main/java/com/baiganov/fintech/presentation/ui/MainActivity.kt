package com.baiganov.fintech.presentation.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.util.NetworkManager
import com.baiganov.fintech.presentation.ui.channels.ChannelsFragment
import com.baiganov.fintech.presentation.ui.people.PeopleFragment
import com.baiganov.fintech.presentation.ui.profile.ProfileFragment
import com.baiganov.fintech.presentation.util.BottomNavigationPages
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var notification: TextView

    private val component by lazy {
        (application as App).component
    }

    @Inject
    lateinit var networkManager: NetworkManager

    override fun onDestroy() {
        super.onDestroy()
        networkManager.unregisterCallback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        networkManager.registerCallback()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        notification = findViewById(R.id.notification)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_fragment_container, ChannelsFragment.newInstance(), BottomNavigationPages.CHANNELS.name)
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> openFragment(BottomNavigationPages.CHANNELS)
                R.id.people_menu_item -> openFragment(BottomNavigationPages.PEOPLE)
                R.id.profile_menu_item -> openFragment(BottomNavigationPages.PROFILE)
                else -> false
            }
        }

        networkManager.isConnectedNetwork.observe(this) { isNetwork ->
            notification.isVisible = !isNetwork
        }
    }

    private fun setFragment(page: BottomNavigationPages): Boolean {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        val channelsFragment = supportFragmentManager.findFragmentByTag(BottomNavigationPages.CHANNELS.name)
        val profileFragment = supportFragmentManager.findFragmentByTag(BottomNavigationPages.PROFILE.name)
        val peopleFragment = supportFragmentManager.findFragmentByTag(BottomNavigationPages.PEOPLE.name)

        when (page) {
            BottomNavigationPages.CHANNELS -> {
                channelsFragment?.let {
                    transaction.show(channelsFragment)
                }
                profileFragment?.let {
                    transaction.hide(profileFragment)
                }
                peopleFragment?.let {
                    transaction.hide(peopleFragment)
                }
            }
            BottomNavigationPages.PEOPLE -> {
                channelsFragment?.let {
                    transaction.hide(channelsFragment)
                }
                profileFragment?.let {
                    transaction.hide(profileFragment)
                }
                peopleFragment?.let {
                    transaction.show(peopleFragment)
                }
            }
            BottomNavigationPages.PROFILE -> {
                channelsFragment?.let {
                    transaction.hide(channelsFragment)
                }
                profileFragment?.let {
                    transaction.show(profileFragment)
                }
                peopleFragment?.let {
                    transaction.hide(peopleFragment)
                }
            }
        }
        transaction.commit()
        return true
    }

    private fun openFragment(page: BottomNavigationPages): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag(page.name)

        if (fragment != null) {
            setFragment(page)
        } else {
            supportFragmentManager.beginTransaction()
                .addToBackStack(page.name)
                .add(
                    R.id.main_fragment_container,
                    newFragmentInstance(page),
                    page.name
                )
                .commit()
        }
        return true
    }

    private fun newFragmentInstance(page: BottomNavigationPages): Fragment = when (page) {
        BottomNavigationPages.CHANNELS -> ChannelsFragment.newInstance()
        BottomNavigationPages.PEOPLE -> PeopleFragment.newInstance()
        BottomNavigationPages.PROFILE -> ProfileFragment.newInstance()
    }
}