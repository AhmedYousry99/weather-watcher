package com.senseicoder.weatherwatcher.utils.global

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.senseicoder.weatherwatcher.features.drawer.home.HomeFragment
import com.senseicoder.weatherwatcher.features.drawer.home.HomeFragment.Companion
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationUtils private constructor(private val application: Application){

    private val _locationState: MutableStateFlow<CurrentState<CurrentLocation>> = MutableStateFlow(
        CurrentState.Loading()
    )

    val locationState = _locationState.asStateFlow()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun getFreshLocation(priority: Int) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(application)
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    //stops it from continuously updating location
                    fusedLocationProviderClient.removeLocationUpdates(this)
                    val latitude = locationResult.locations.get(0).latitude
                    val longitude = locationResult.locations.get(0).longitude
                    /*openSmsBtn.apply {
                        isEnabled = true
                        setOnClickListener{
                            composeMmsMessage(addressAsDescription )
                        }
                    }
                    openMapBtn.apply {
                        isEnabled = true
                        setOnClickListener{
                            fragment.updateMarker(GeoPoint(latitude, longitude))
                        }
                    }*/
                    Log.d(TAG, "onLocationResult: Longitude: $longitude, Latitude: $latitude")
                    _locationState.value = CurrentState.Success(CurrentLocation(longitude = longitude, latitude = latitude))
                }
            },
            Looper.myLooper()
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun arePermissionsGranted(): Boolean {
        return application.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || application.checkSelfPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /*private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            HomeFragment.REQUEST_LOCATION_CODE
        )
    }*/

    private fun enableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(application, intent, null)
    }

    companion object{
        private const val REQUEST_LOCATION_CODE: Int = 2005
        private const val TAG = "LocationUtils"

        @Volatile
        private var instance: LocationUtils? = null

        fun getInstance(application: Application): LocationUtils{
            return instance ?: synchronized(this){
                val instance = LocationUtils(application)
                this.instance = instance
                instance
            }
        }
    }

    data class CurrentLocation(val longitude: Double, val latitude: Double)
}