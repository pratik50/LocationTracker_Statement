package com.pratik.ekattatrackers.dataModel

data class LocationModel (
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)