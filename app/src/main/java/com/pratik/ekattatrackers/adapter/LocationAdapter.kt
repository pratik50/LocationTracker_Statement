package com.pratik.ekattatrackers.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pratik.ekattatrackers.dataModel.LocationModel
import com.pratik.ekattatrackers.databinding.ItemLocationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationAdapter(private val locationList: MutableList<LocationModel>) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {


    inner class LocationViewHolder(val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]

        val formattedTime = SimpleDateFormat("hh:mm a, dd MMM yyyy", Locale.getDefault()).format(
            Date(location.timestamp)
        )

        with(holder.binding) {
            tvLatitude.text = "Latitude: ${location.latitude}"
            tvLongitude.text = "Longitude: ${location.longitude}"
            tvTime.text = formattedTime
        }
    }

    override fun getItemCount() = locationList.size
}