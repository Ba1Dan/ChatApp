package com.baiganov.fintech.presentation.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.data.model.response.User
import com.baiganov.fintech.util.State
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    private lateinit var tvName: TextView
    private lateinit var tvIsOnline: TextView
    private lateinit var progressBar: ProgressBar

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>

    private val presenter: ProfilePresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvName = view.findViewById(R.id.profile_name)
        tvIsOnline = view.findViewById(R.id.profile_online_status)
        progressBar = view.findViewById(R.id.pb_profile)
        return view

    }

    override fun render(state: State<User>) {
        when (state) {
            is State.Result -> {
                progressBar.isVisible = false
                setData(state.data)
            }
            is State.Loading -> {
                progressBar.isVisible = true
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                progressBar.isVisible = false
            }
        }
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