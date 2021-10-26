package com.baiganov.fintech.ui.people

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baiganov.fintech.R
import com.baiganov.fintech.data.DataManager
import com.baiganov.fintech.ui.channels.streams.recyclerview.fingerprints.ItemFingerPrint
import com.baiganov.fintech.ui.chat.recyclerview.ItemClickListener


class PeopleFragment : Fragment(), ItemClickListener {

    override fun onItemClick(position: Int, item: ItemFingerPrint) {
        TODO("Not yet implemented")
    }

    private lateinit var adapterPerson: PersonAdapter
    private lateinit var rvUsers: RecyclerView
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
        adapterPerson.setData(dataManager.users)
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            PeopleFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}