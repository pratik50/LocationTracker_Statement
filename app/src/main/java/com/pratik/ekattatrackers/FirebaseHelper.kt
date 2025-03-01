package com.pratik.ekattatrackers

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.pratik.ekattatrackers.dataModel.LocationModel

class FirebaseHelper(private val context: Context, private val locationRef: DatabaseReference) {

    //fn for fetching all data
    fun fetchPreviousLocations(onSuccess: (List<LocationModel>) -> Unit, onFailure: (Exception) -> Unit) {
        locationRef.orderByChild("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val locations = snapshot.children.mapNotNull { it.getValue(LocationModel::class.java) }.sortedByDescending { it.timestamp }
                    onSuccess(locations)
                } else {
                    onFailure(Exception("No data in Firebase"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    //fn to store the data locations
    fun storeLocationInFirebase(location: LocationModel) {
        val key = locationRef.push().key
        if (key != null) {
            locationRef.child(key).setValue(location)
                .addOnSuccessListener {
                    Toast.makeText(context, "Location saved to Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save location to Firebase", Toast.LENGTH_SHORT).show()
                }
        }
    }
}