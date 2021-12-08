package com.baiganov.fintech.presentation.ui.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.App
import com.baiganov.fintech.R
import com.baiganov.fintech.presentation.model.ItemFingerPrint
import com.baiganov.fintech.presentation.model.UserFingerPrint
import com.baiganov.fintech.presentation.ui.chat.recyclerview.ItemClickListener
import com.baiganov.fintech.presentation.ui.people.adapters.PersonAdapter
import com.baiganov.fintech.util.State
import com.facebook.shimmer.ShimmerFrameLayout
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class PeopleFragment : MvpAppCompatFragment(), PeopleView, ItemClickListener {

    private lateinit var adapterPerson: PersonAdapter
    private lateinit var rvUsers: RecyclerView
    private lateinit var shimmer: ShimmerFrameLayout

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
        shimmer = view.findViewById(R.id.shimmer_people)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterPerson = PersonAdapter(this)
        rvUsers.adapter = adapterPerson
    }

    override fun onItemClick(position: Int, item: ItemFingerPrint) {

    }

    override fun render(state: State<List<UserFingerPrint>>) {

        when (state) {
            is State.Result -> {
                adapterPerson.listOfUser = state.data
                shimmer.isVisible = false
            }
            is State.Loading -> {
                shimmer.isVisible = true
            }
            is State.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                shimmer.isVisible = false
            }
        }
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}