package com.baiganov.fintech.presentation.ui.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.model.response.User
import com.baiganov.fintech.presentation.util.Event
import com.baiganov.fintech.presentation.util.State
import javax.inject.Inject

class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModel: ProfileViewModel

    private lateinit var tvName: TextView
    private lateinit var tvIsOnline: TextView
    private lateinit var progressBar: ProgressBar

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.profile_name)
        tvIsOnline = view.findViewById(R.id.profile_online_status)
        progressBar = view.findViewById(R.id.pb_profile)
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

    companion object {
        fun newInstance() = ProfileFragment()
    }
}