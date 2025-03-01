package com.pratik.ekattatrackers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    //Showing fetched location (lon & lan) Notification
     fun showLocationNotification(latitude: Double, longitude: Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location_channel",
                "Location Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows notifications when location is fetched"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, "location_channel")
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle("New Location Fetched")
            .setContentText("Lat: $latitude, Long: $longitude")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }
}