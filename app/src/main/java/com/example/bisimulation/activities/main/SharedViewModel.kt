package com.example.bisimulation.activities.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bisimulation.repository.FirestoreRepository
import com.example.bisimulation.repository.FsGetIntEventListener
import com.example.bisimulation.repository.RealtimeRepository
import com.example.bisimulation.repository.RtGetIntEventListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {
    // User information
    private val auth = Firebase.auth
    val currentUser = auth.currentUser
    val uid = currentUser?.uid
    val email = currentUser?.email

    private val _name = MutableLiveData<String>()
    private val _surname = MutableLiveData<String>()
    private val _username = MutableLiveData<String>()
    private val _victories = MutableLiveData<Int>()
    private val _losses = MutableLiveData<Int>()
    private val _activeUsers = MutableLiveData<Int>()
    val name: LiveData<String> = _name
    val surname: LiveData<String> = _surname
    val username: LiveData<String> = _username
    val victories: LiveData<Int> = _victories
    val losses: LiveData<Int> = _losses
    val activeUsers: LiveData<Int> = _activeUsers

    init {
        // Get all the user extra info
        if (currentUser != null) {
            viewModelScope.launch {
                _name.value = FirestoreRepository.getName(currentUser.uid)
                _surname.value = FirestoreRepository.getSurname(currentUser.uid)
                _username.value = FirestoreRepository.getUsername(currentUser.uid)
            }

            // Updates victories and losses count in real time
            FirestoreRepository.getStatsReference(currentUser.uid).addSnapshotListener(
                FsGetIntEventListener(_victories, "victories")
            )
            FirestoreRepository.getStatsReference(currentUser.uid).addSnapshotListener(
                FsGetIntEventListener(_losses, "losses")
            )

            // Get the currently active users in real time
            RealtimeRepository.activeUsersListRef.addValueEventListener(
                RtGetIntEventListener(
                    _activeUsers
                )
            )
        }
    }

    fun manageOnlinePresence(username: String){
        if (currentUser?.uid != null){
            RealtimeRepository.setupUserPresence(currentUser.uid, username)
        }
    }
}
