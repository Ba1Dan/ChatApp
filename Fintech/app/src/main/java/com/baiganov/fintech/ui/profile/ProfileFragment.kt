package com.baiganov.fintech.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.R
import com.baiganov.fintech.data.ProfileRepository
import com.baiganov.fintech.data.StreamRepository
import com.baiganov.fintech.data.db.DatabaseModule
import com.baiganov.fintech.data.db.StreamsDao
import com.baiganov.fintech.data.network.NetworkModule
import com.baiganov.fintech.model.response.User
import com.baiganov.fintech.ui.Event
import com.baiganov.fintech.ui.channels.ChannelsViewModel
import com.baiganov.fintech.ui.channels.ChannelsViewModelFactory
import com.baiganov.fintech.util.State

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var tvName: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvIsOnline: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.profile_name)
        tvIsOnline = view.findViewById(R.id.profile_online_status)
        progressBar = view.findViewById(R.id.pb_profile)
        setupViewModel()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.profile.observe(viewLifecycleOwner, {
            handleState(it)
        })
        viewModel.obtainEvent(Event.EventProfile.LoadProfile)
    }

    private fun handleState(it: State<User>) {
        when (it) {
            is State.Result -> {
                progressBar.isVisible = false
                setData(it.data)
            }
            is State.Loading -> {
                progressBar.isVisible = true
            }
            is State.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                progressBar.isVisible = false
            }
        }
    }

    private fun setData(profile: User) {
        tvName.text = profile.fullName
        tvIsOnline.text =
            if (profile.isActive) requireContext().getString(R.string.status_active)
            else requireContext().getString(R.string.status_offline)
    }

    private fun setupViewModel() {
        val networkModule = NetworkModule()
        val service = networkModule.create()

        val viewModelFactory =
            ProfileViewModelFactory(ProfileRepository(service = service))

        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ProfileViewModel::class.java)
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}