package com.example.bisimulation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bisimulation.R
import com.example.bisimulation.activities.main.fragments.PlayNowFragmentDirections
import com.example.bisimulation.utils.MatchmakingRoomModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class RoomFirestoreRecyclerAdapter(options: FirestoreRecyclerOptions<MatchmakingRoomModel>, private val userUid: String, private val roomClickListener: RoomClickListener) :
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
        holder.setRoomHost(model.player1username)

        holder.connectButton.setOnClickListener {
//            val action = PlayNowFragmentDirections.actionPlayNowToLobby(
//                player1uid = model.player1uid,
//                player2uid = userUid
//            )
            // navigate
            //roomClickListener.navigate(action)
        }
    }

    inner class RoomViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        val connectButton: Button = view.findViewById(R.id.joinRoom_button)

//        init {
//            connectButton.setOnClickListener {
//                roomClickListener
//            }
//        }

        internal fun setRoomHost(username: String) {
            val roomHostTextView = view.findViewById<TextView>(R.id.hostUsername_textView)
            roomHostTextView.text = username
        }
    }
}