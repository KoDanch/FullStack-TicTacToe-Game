package com.example.tictactoe.geolocation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.tictactoe.callback.game_service_callback.GameHandler
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@SuppressLint("MissingPermission")
@Composable
fun GeolocationService() {
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val gameHandler = GameHandler


    val locationRequest = LocationRequest.Builder(5000L)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMinUpdateDistanceMeters(200f)
        .build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult.lastLocation != null) {
                gameHandler.notifyGetLocationSucces("${locationResult.lastLocation!!.latitude},${locationResult.lastLocation!!.longitude}")
            }

        }
    }

    try {
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            .addOnFailureListener { e ->
                gameHandler.notifyGetLocationFail(e.localizedMessage ?: "Error")
            }
    } catch (e: Exception) {
        gameHandler.notifyGetLocationFail(e.stackTraceToString())
    }
}