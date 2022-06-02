package com.example.pj_projekt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class logAdapter(private val data: ArrayList<logStructure>): RecyclerView.Adapter<logAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_list, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTV: TextView = itemView.findViewById(R.id.usernameTV)
        val dateTV: TextView = itemView.findViewById(R.id.dateTV)
        val successTV: TextView = itemView.findViewById(R.id.successTV)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = data[position]
        holder.usernameTV.text = model.username
        holder.dateTV.text = model.date
        holder.successTV.text = model.success.toString()
    }
}