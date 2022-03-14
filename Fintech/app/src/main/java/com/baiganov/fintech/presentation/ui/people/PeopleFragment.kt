package com.baiganov.fintech.presentation.ui.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.baiganov.fintech.App
import com.baiganov.fintech.databinding.FragmentPeopleBinding
import com.baiganov.fintech.presentation.ViewModelFactory
import com.baiganov.fintech.presentation.model.UserFingerPrint
import com.baiganov.fintech.presentation.ui.base.BaseFragment
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.presentation.ui.chat.recyclerview.TypeItemClickStream
import com.baiganov.fintech.presentation.ui.people.adapters.PersonAdapter
import com.baiganov.fintech.presentation.util.State
import javax.inject.Inject

class PeopleFragment : BaseFragment<FragmentPeopleBinding>(), ItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapterPerson: PersonAdapter
    private lateinit var viewModel: PeopleViewModel

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPeopleBinding {
        return FragmentPeopleBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory)[PeopleViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterPerson = PersonAdapter(this)
        binding.rvUsers.adapter = adapterPerson

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.searchUsers(p0.orEmpty())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.searchUsers(p0.orEmpty())
                return true
            }
        })

        viewModel.loadUsers()
        viewModel.searchUsers("")
        viewModel.state.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onItemClick(click: TypeItemClickStream) {

    }

    private fun render(state: State<List<UserFingerPrint>>) {
        when (state) {
            is State.Result -> {
                adapterPerson.listOfUser = state.data
                binding.shimmer.root.isVisible = false
            }
            is State.Loading -> {
                binding.shimmer.root.isVisible = true
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                binding.shimmer.root.isVisible = false
            }
        }
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}