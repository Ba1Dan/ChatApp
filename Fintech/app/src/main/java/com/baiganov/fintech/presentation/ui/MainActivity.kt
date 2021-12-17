package com.baiganov.fintech.presentation.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.NetworkManager
import com.baiganov.fintech.presentation.ui.channels.ChannelsFragment
import com.baiganov.fintech.presentation.ui.people.PeopleFragment
import com.baiganov.fintech.presentation.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var channelsFragment: ChannelsFragment
    private lateinit var peopleFragment: PeopleFragment
    private lateinit var profileFragment: ProfileFragment
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

            channelsFragment = ChannelsFragment.newInstance()
            peopleFragment = PeopleFragment.newInstance()
            profileFragment = ProfileFragment.newInstance()

            supportFragmentManager.commit {
                add(
                    R.id.main_fragment_container,
                    channelsFragment,
                    BottomNavigationPages.CHANNELS.name
                )
                add(
                    R.id.main_fragment_container,
                    peopleFragment,
                    BottomNavigationPages.CHANNELS.name
                )
                add(
                    R.id.main_fragment_container,
                    profileFragment,
                    BottomNavigationPages.CHANNELS.name
                )
            }

            setFragment(BottomNavigationPages.CHANNELS)
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> setFragment(BottomNavigationPages.CHANNELS)
                R.id.people_menu_item -> setFragment(BottomNavigationPages.PEOPLE)
                R.id.profile_menu_item -> setFragment(BottomNavigationPages.PROFILE)
                else -> false
            }
        }
        networkManager.isConnectedNetwork.observe(this, { isNetwork ->
            notification.isVisible = !isNetwork
        })
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