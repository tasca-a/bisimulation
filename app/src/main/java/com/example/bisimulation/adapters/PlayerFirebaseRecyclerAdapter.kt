package com.example.bisimulation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bisimulation.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PlayerFirebaseRecyclerAdapter(options: FirebaseRecyclerOptions<String>) :
    FirebaseRecyclerAdapter<String, PlayerFirebaseRecyclerAdapter.UsernameViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsernameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return UsernameViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UsernameViewHolder,
        position: Int,
        model: String
    ) {
        holder.setUsername(model)
    }

    inner class UsernameViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {
        internal fun setUsername(username: String) {
            val playerUsernameTextView = view.findViewById<TextView>(R.id.playerUsername_textView)
            playerUsernameTextView.text = username
        }
    }
}