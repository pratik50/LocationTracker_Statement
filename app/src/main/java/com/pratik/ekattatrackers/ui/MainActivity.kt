package com.pratik.ekattatrackers.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.pratik.ekattatrackers.FirebaseHelper
import com.pratik.ekattatrackers.GoogleAuthClient
import com.pratik.ekattatrackers.LocationHelper
import com.pratik.ekattatrackers.NotificationHelper
import com.pratik.ekattatrackers.R
import com.pratik.ekattatrackers.adapter.LocationAdapter
import com.pratik.ekattatrackers.dataModel.LocationModel
import com.pratik.ekattatrackers.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationAdapter: LocationAdapter
    private val locationList = mutableListOf<LocationModel>()
    private lateinit var googleAuthClient: GoogleAuthClient
    private lateinit var firebaseHelper: FirebaseHelper
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var locationHelper: LocationHelper
    private lateinit var shimmerLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shimmerLayout = binding.shimmerLayout

        setSupportActionBar(binding.toolbar)
        FirebaseApp.initializeApp(this)

        googleAuthClient = GoogleAuthClient(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRef = FirebaseDatabase.getInstance().getReference("locations")

        notificationHelper = NotificationHelper(this)
        firebaseHelper = FirebaseHelper(this, locationRef)
        locationHelper = LocationHelper(this, fusedLocationProviderClient)

        locationAdapter = LocationAdapter(locationList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = locationAdapter

        // Set up location updates listener
        locationHelper.onLocationUpdated = { location ->
            val latitude = location.latitude
            val longitude = location.longitude

            val locationModel = LocationModel(latitude, longitude)
            locationList.add(0, locationModel)
            locationAdapter.notifyItemInserted(0)
            binding.recyclerView.scrollToPosition(0)

            firebaseHelper.storeLocationInFirebase(locationModel)
            notificationHelper.showLocationNotification(latitude, longitude)
        }

        fetchPreviousLocations()
        getCurrentLocation()
    }

    //fn to retrieve firebase data
    private fun fetchPreviousLocations() {

        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()

        binding.recyclerView.visibility = View.GONE

        firebaseHelper.fetchPreviousLocations(
            onSuccess = { locations ->
                locationList.clear()
                locationList.addAll(locations)
                locationAdapter.notifyDataSetChanged()

                shimmerLayout.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            },
            onFailure = { exception ->
                Toast.makeText(this, "Failed to fetch locations: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    //When app will be in Resume State
    override fun onResume() {
        super.onResume()
        if (locationHelper.checkPermission()) {
            if (locationHelper.checkLocationEnabled()) {
                // if enabled, fetch location
                getCurrentLocation()
            } else {
                // else show the dialog again
                locationHelper.showLocationEnableDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationHelper.stopPeriodicLocationUpdates()
    }

    //main fn call
    private fun getCurrentLocation() {
        if (locationHelper.checkPermission()) {
            if (locationHelper.checkLocationEnabled()) {
                locationHelper.startPeriodicLocationUpdates()
            }
        } else {
            locationHelper.requestPermission()
        }
    }

    //Location permission handler
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Allow Permission to use this app", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    //Toolbar menu handler
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                lifecycleScope.launch {
                    googleAuthClient.signOut()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}