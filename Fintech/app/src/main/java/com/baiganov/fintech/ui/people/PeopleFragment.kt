package com.baiganov.fintech.ui.people

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.DataManager
import com.baiganov.fintech.ui.MainScreenState
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.ui.people.adapters.PersonAdapter
import com.baiganov.fintech.ui.people.adapters.UserFingerPrint
import com.todkars.shimmer.ShimmerRecyclerView


class PeopleFragment : Fragment(), ItemClickListener {

    private val viewModel: PeopleViewModel by activityViewModels()

    private lateinit var adapterPerson: PersonAdapter
    private lateinit var rvUsers: ShimmerRecyclerView
    private lateinit var dataManager: DataManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        rvUsers = view.findViewById(R.id.rv_users)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataManager = DataManager()
        adapterPerson = PersonAdapter(this)
        rvUsers.adapter = adapterPerson
        viewModel.users.observe(viewLifecycleOwner, {
            processMainScreenState(it)
        })
        viewModel.loadUsers()
//        adapterPerson.listOfUser = dataManager.users
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        TODO("Not yet implemented")
    }

    private fun processMainScreenState(it: MainScreenState?) {
        when (it) {
            is MainScreenState.Result -> {
//                if (it.items.isEmpty()) {
//                    frameNotResult.isVisible = true
//                }
                adapterPerson.listOfUser = it.items as List<UserFingerPrint>
                rvUsers.hideShimmer()
            }
            MainScreenState.Loading -> {
//                frameNotResult.isVisible = false
                rvUsers.showShimmer()
            }
            is MainScreenState.Error -> {
                Toast.makeText(requireContext(), it.error.message, Toast.LENGTH_SHORT).show()
//                frameNotResult.isVisible = false
                rvUsers.hideShimmer()
            }
        }
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}