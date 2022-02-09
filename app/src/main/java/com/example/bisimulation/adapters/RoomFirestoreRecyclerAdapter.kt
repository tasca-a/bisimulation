package com.example.bisimulation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bisimulation.R
import com.example.bisimulation.utils.MatchmakingRoomModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class RoomFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<MatchmakingRoomModel>) :
    FirestoreRecyclerAdapter<MatchmakingRoomModel, RoomFirestoreRecyclerAdapter.RoomViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return RoomViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RoomViewHolder,
        position: Int,
        model: MatchmakingRoomModel
    ) {
        holder.setRoomHost(model.roomHost)
    }

    inner class RoomViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {
        internal fun setRoomHost(username: String) {
            val roomHostTextView = view.findViewById<TextView>(R.id.roomHost_textView)
            roomHostTextView.text = username
        }
    }
}