package com.pratik.ekattatrackers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pratik.ekattatrackers.R
import com.pratik.ekattatrackers.dataModel.LocationModel

class LocationAdapter(private val locationList: MutableList<LocationModel>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val latText: TextView = itemView.findViewById(R.id.tvLatitude)
        val lonText: TextView = itemView.findViewById(R.id.tvLongitude)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.latText.text = "Latitude: ${location.latitude}"
        holder.lonText.text = "Longitude: ${location.longitude}"
    }

    override fun getItemCount() = locationList.size
}