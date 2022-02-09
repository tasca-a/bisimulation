package com.example.bisimulation.activities.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bisimulation.adapters.PlayerFirebaseRecyclerAdapter
import com.example.bisimulation.databinding.FragmentActivePlayersBinding
import com.example.bisimulation.repository.RealtimeRepository
import com.firebase.ui.database.FirebaseRecyclerOptions

class ActivePlayersFragment : Fragment() {
    private lateinit var binding: FragmentActivePlayersBinding

    //Firestore adapter
    private lateinit var adapter: PlayerFirebaseRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activePlayersFragmentSetup(inflater, container)
        firebaseAdapterInitialization()

        binding.activePlayerRecyclerView.adapter = adapter

        return binding.root
    }

    private fun firebaseAdapterInitialization() {
        val query = RealtimeRepository.getActiveUsersListReference()
        val options = FirebaseRecyclerOptions.Builder<String>()
            .setQuery(query, String::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()
        adapter = PlayerFirebaseRecyclerAdapter(options)
    }

    private fun activePlayersFragmentSetup(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentActivePlayersBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }
}