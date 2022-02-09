package com.example.bisimulation.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.bisimulation.activities.main.SharedViewModel
import com.example.bisimulation.adapters.RoomFirestoreRecyclerAdapter
import com.example.bisimulation.databinding.FragmentPlayNowBinding
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.utils.MatchmakingRoomModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class PlayNowFragment : Fragment() {
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

        return binding.root
    }

    private fun firestoreAdapterInitialization() {
        val query = FirestoreRepository.getRoomsReference()
        val options = FirestoreRecyclerOptions.Builder<MatchmakingRoomModel>()
            .setQuery(query, MatchmakingRoomModel::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
        adapter = RoomFirestoreRecyclerAdapter(options)
    }

    private fun playNowFragmentSetup(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        binding = FragmentPlayNowBinding.inflate(inflater, container, false)
        binding.sharedViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}