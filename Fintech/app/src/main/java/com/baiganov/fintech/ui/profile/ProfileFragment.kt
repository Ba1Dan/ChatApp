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
import com.baiganov.fintech.R
import com.baiganov.fintech.model.Profile
import com.baiganov.fintech.ui.State


class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by activityViewModels()

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
        tvStatus = view.findViewById(R.id.profile_status)
        tvIsOnline = view.findViewById(R.id.profile_online_status)
        progressBar = view.findViewById(R.id.pb_profile)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.profile.observe(viewLifecycleOwner, {
            processMainScreenState(it)
        })
        viewModel.loadProfile()
    }

    private fun processMainScreenState(it: State<Profile>) {
        when (it) {
            is State.Result -> {
                progressBar.isVisible = false
                val profile = it.data
                tvName.text = profile.name
                tvStatus.text = profile.status
                tvIsOnline.text = if (profile.isOnline) "online" else "offline"

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

    companion object {
        fun newInstance() = ProfileFragment()
    }
}