package com.pratik.ekattatrackers

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pratik.ekattatrackers.ui.MainActivity
import com.pratik.ekattatrackers.ui.MainActivity.Companion.PERMISSION_REQUEST_ACCESS_LOCATION

class LocationHelper(   private val context: Context,
                        private val fusedLocationProviderClient: FusedLocationProviderClient
) {

    private val locationUpdateInterval = 20 * 1000L
    private val handler = Handler(Looper.getMainLooper())
    private val dialog: BottomSheetDialog = BottomSheetDialog(context)
    private val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_loader, null)

    var onLocationUpdated: ((Location) -> Unit)? = null

    init {
        dialog.setContentView(view)
        dialog.setCancelable(false)
    }

    //fetching location for every 5 min
    fun startPeriodicLocationUpdates() {
        if (!checkPermission()) {
            requestPermission()
            return
        }

        // Fetch location immediately
        fetchLocation()

    }

    // Fetch a single location update
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {

        dialog.show()

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1
            interval = 0
            fastestInterval = 0
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // Stop periodic location updates
    fun stopPeriodicLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        handler.removeCallbacksAndMessages(null)
    }


    //call back fn for startPeriodicLocationUpdates
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location: Location? = locationResult.lastLocation

            if (location != null) {

                onLocationUpdated?.invoke(location)

                dialog.dismiss()

                // Schedule the next location update
                handler.postDelayed({
                    startPeriodicLocationUpdates()
                }, locationUpdateInterval)
            }
        }
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(context as AppCompatActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //dialog box for enabling the location
    fun showLocationEnableDialog() {
        AlertDialog.Builder(context)
            .setTitle("Location Required")
            .setMessage("Please enable location to use this application.")
            .setPositiveButton("Enable") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(context, "Location is required to use this app", Toast.LENGTH_SHORT).show()
                (context as AppCompatActivity).finish()
            }
            .setCancelable(false)
            .show()
    }

}