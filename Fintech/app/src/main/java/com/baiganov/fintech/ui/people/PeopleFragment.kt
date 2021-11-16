package com.baiganov.fintech.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.baiganov.fintech.R
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.ui.people.adapters.PersonAdapter
import com.baiganov.fintech.ui.people.adapters.UserFingerPrint
import com.baiganov.fintech.util.State
import com.todkars.shimmer.ShimmerRecyclerView


class PeopleFragment : Fragment(), ItemClickListener {

    private val viewModel: PeopleViewModel by activityViewModels()

    private lateinit var adapterPerson: PersonAdapter
    private lateinit var rvUsers: ShimmerRecyclerView

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
        adapterPerson = PersonAdapter(this)
        rvUsers.adapter = adapterPerson
        viewModel.users.observe(viewLifecycleOwner, {
            processMainScreenState(it)
        })
        viewModel.getUsers()
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {

    }

    private fun processMainScreenState(it: State<List<UserFingerPrint>>) {
        when (it) {
            is State.Result -> {
                adapterPerson.listOfUser = it.data
                rvUsers.hideShimmer()
            }
            is State.Loading -> {
                rvUsers.showShimmer()
            }
            is State.Error -> {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                rvUsers.hideShimmer()
            }
        }
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}