package com.example.pj_projekt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pj_projekt.data.Location

class LocationAdapter (private val data: ArrayList<Location>, private val onClickObject: MyOnClick): RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    interface MyOnClick {
        fun onClick(p0: View?, position:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.locationTitle)!!
        val coords: TextView = itemView.findViewById(R.id.locationCoordinates)!!
        val line: CardView = itemView.findViewById(R.id.cvLocation)!!
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = data[position]
        val model = data[position]
        holder.name.text = model.getName()
        holder.coords.text = model.getCoordLat().toString() + ", " + model.getCoordLong().toString()

        if(!itemsViewModel.isSelected()){
            holder.line.setCardBackgroundColor(Color.WHITE)
        }
        holder.line.setOnClickListener { p0 ->
            holder.line.setCardBackgroundColor(Color.YELLOW)
            onClickObject.onClick(p0, holder.adapterPosition)
        }
    }
}