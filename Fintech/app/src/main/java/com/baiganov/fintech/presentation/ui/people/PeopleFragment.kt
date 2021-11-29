package com.baiganov.fintech.presentation.ui.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.presentation.ui.people.adapters.PersonAdapter
import com.baiganov.fintech.presentation.ui.people.adapters.UserFingerPrint
import com.baiganov.fintech.util.State
import com.todkars.shimmer.ShimmerRecyclerView
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class PeopleFragment : MvpAppCompatFragment(), PeopleView, ItemClickListener {

    private lateinit var adapterPerson: PersonAdapter
    private lateinit var rvUsers: ShimmerRecyclerView

    @Inject
    lateinit var presenterProvider: Provider<PeoplePresenter>

    private val presenter: PeoplePresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as App).component.inject(this)
    }

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

    override fun render(state: State<List<UserFingerPrint>>) {

        when (state) {
            is State.Result -> {
                adapterPerson.listOfUser = state.data
                rvUsers.hideShimmer()
            }
            is State.Loading -> {
                rvUsers.showShimmer()
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                rvUsers.hideShimmer()
            }
        }
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}