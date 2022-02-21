package com.example.bisimulation.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.bisimulation.activities.main.SharedViewModel
import com.example.bisimulation.adapters.RoomClickListener
import com.example.bisimulation.adapters.RoomFirestoreRecyclerAdapter
import com.example.bisimulation.databinding.FragmentPlayNowBinding
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.model.Lobby
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlayNowFragment : Fragment(), RoomClickListener {
    private lateinit var binding: FragmentPlayNowBinding
    private val viewModel: SharedViewModel by activityViewModels()

    // Firestore adapter
    private lateinit var adapter: RoomFirestoreRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        playNowFragmentSetup(inflater, container)
        firestoreAdapterInitialization()

        binding.roomRecyclerView.adapter = adapter

        binding.createRoomButton.setOnClickListener {
            val action = PlayNowFragmentDirections.actionPlayNowToP1lobby(
                viewModel.uid!!,
                viewModel.username.value!!
            )
            findNavController().navigate(action)
        }

        return binding.root
    }

    private fun firestoreAdapterInitialization() {
        val query = FirestoreRepository.getRoomsReference()
        val options = FirestoreRecyclerOptions.Builder<Lobby>()
            .setQuery(query, Lobby::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        //TODO: fix, sometimes it trows a null pointer exception
        adapter = RoomFirestoreRecyclerAdapter(options, viewModel.uid!!, viewModel.username.value!!, this)
    }

    private fun playNowFragmentSetup(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentPlayNowBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun navigate(action: NavDirections) {
        findNavController().navigate(action)
    }
}