package com.baiganov.fintech.presentation.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.data.model.User
import com.baiganov.fintech.databinding.FragmentProfileBinding
import com.baiganov.fintech.presentation.ViewModelFactory
import com.baiganov.fintech.presentation.ui.base.BaseFragment
import com.baiganov.fintech.presentation.util.State
import javax.inject.Inject

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: ProfileViewModel

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[ProfileViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) {
            handleState(it)
        }
    }

    private fun handleState(state: State<User>) {
        when (state) {
            is State.Result -> {
                binding.pbProfile.isVisible = false
                setData(state.data)
            }
            is State.Loading -> {
                binding.pbProfile.isVisible = true
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                binding.pbProfile.isVisible = false
            }
        }
    }

    private fun setData(profile: User) {
        binding.profileName.text = profile.fullName
        binding.profileOnlineStatus.text =
            if (profile.isActive) requireContext().getString(R.string.status_active)
            else requireContext().getString(R.string.status_offline)
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}