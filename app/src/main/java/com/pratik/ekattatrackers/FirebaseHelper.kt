package com.pratik.ekattatrackers

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.pratik.ekattatrackers.dataModel.LocationModel
class FirebaseHelper(
    private val context: Context,
    private val uid: String
) {

    private val userLocationRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("user_locations").child(uid)

    // Function to fetch user-specific locations
    fun fetchPreviousLocations(
        onSuccess: (List<LocationModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userLocationRef.orderByChild("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val locations = snapshot.children.mapNotNull { it.getValue(LocationModel::class.java) }
                        .sortedByDescending { it.timestamp }
                    onSuccess(locations)
                } else {

                    onFailure(Exception("No data in Firebase"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Function to store user-specific locations
    fun storeLocationInFirebase(location: LocationModel) {
        val key = userLocationRef.push().key
        if (key != null) {
            userLocationRef.child(key).setValue(location)
                .addOnSuccessListener {
                    Toast.makeText(context, "Location saved to Firebase", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save location to Firebase", Toast.LENGTH_SHORT).show()
                }
        }
    }
}