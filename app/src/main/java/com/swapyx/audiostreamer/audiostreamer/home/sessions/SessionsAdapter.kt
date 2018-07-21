package com.swapyx.audiostreamer.audiostreamer.home.sessions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.swapyx.audiostreamer.audiostreamer.R
import com.swapyx.audiostreamer.audiostreamer.data.audioserver.model.Session
import kotlinx.android.synthetic.main.past_session_layout.view.*

class SessionsAdapter(
        val listener: (Session) -> Unit
) : RecyclerView.Adapter<SessionsAdapter.SessionHolder>() {

    private val pastSessionsList = ArrayList<Session>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.past_session_layout, parent, false)
        return SessionHolder(v, listener)
    }

    override fun getItemCount(): Int {
        return pastSessionsList.size
    }

    override fun onBindViewHolder(holder: SessionHolder, position: Int) {
        holder.bind(pastSessionsList[position])
    }

    fun updateList(list: List<Session>) {
        if (pastSessionsList.isNotEmpty()){
            pastSessionsList.clear()
        }
        pastSessionsList.addAll(list)
        notifyDataSetChanged()
    }

    class SessionHolder(
            itemView: View,
            listener: (Session) -> Unit
    ): RecyclerView.ViewHolder(itemView) {
        private val sessionName: TextView = itemView.item_session_name
        private val sessionStatus: TextView = itemView.item_session_status
        private val sessionTimestamp: TextView = itemView.item_session_timestamp

        var item: Session? = null

        init {
            itemView.setOnClickListener {
                item?.let { listener(it) }
            }
        }

        fun bind(session: Session) {
            sessionName.text = session.sid
            sessionStatus.text = session.processed.toString()
            sessionTimestamp.text = session.timestamp.toString()
            item = session
        }
    }
}