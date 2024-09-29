package com.senseicoder.weatherwatcher.features.drawer.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.FragmentHomeBinding
import com.senseicoder.weatherwatcher.features.drawer.home.adapters.DailyAdapter
import com.senseicoder.weatherwatcher.features.drawer.home.adapters.HourlyAdapter
import com.senseicoder.weatherwatcher.features.drawer.home.viewmodel.HomeViewModel
import com.senseicoder.weatherwatcher.features.drawer.home.viewmodel.HomeViewModelFactory
import com.senseicoder.weatherwatcher.models.repositories.WeatherRepositoryImpl
import com.senseicoder.weatherwatcher.network.RemoteDataSourceImpl
import com.senseicoder.weatherwatcher.utils.global.Constants
import com.senseicoder.weatherwatcher.utils.global.Constants.SharedPrefs.Settings.Location.LOCATION
import com.senseicoder.weatherwatcher.utils.global.toDateTime
import com.senseicoder.weatherwatcher.utils.global.toDrawable2X
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeFragment : Fragment()/*, ActivityCompat.OnRequestPermissionsResultCallback*/{

    private lateinit var binding: FragmentHomeBinding
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var snackBar : Snackbar
    private lateinit var settings: SharedPreferences

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // To log permissions:
        permissions.entries.forEach {
            Log.d("DEBUG", "${it.key} = ${it.value}")
        }

        val a: Boolean = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] as Boolean
        val b: Boolean = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] as Boolean

        if (a || b) {
            if (isLocationEnabled()) {
                Log.d(TAG, "onRequestPermissionsResult: getting fresh location...")
                getFreshLocation()
            } else {
                Log.d(TAG, "onRequestPermissionsResult: requesting location services")
                enableLocationServices()
            }
        }else{
            binding.apply {
                permissionsDeniedGroup.visibility = View.VISIBLE
                permissionsGrantedGroup.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        }
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            //setting background in xml doesn't work for some reason...
            heroCard.setBackgroundResource(R.drawable.primary_gradient_circular_radius_background)
            permissionDeniedAllowButton.setOnClickListener { requestLocationPermissions() }
        }
        hourlyAdapter = HourlyAdapter()
        dailyAdapter = DailyAdapter()

        /*val tempList: List<WeatherDTO> = listOf(
            WeatherDTO(
                temperature = "16",
                minTemperature = "15",
                maxTemperature = "18",
                description = "moderate rain",
                cloudiness = "83",
                humidity = "60",
                pressure = "1021",
                windSpeed = "4.09",
                visibility =  "10000",
                tempIcon = "01d"
            ),
            WeatherDTO(
                id = 1,
                temperature = "16",
                minTemperature = "15",
                maxTemperature = "18",
                description = "moderate rain",
                cloudiness = "83",
                humidity = "60",
                pressure = "1021",
                windSpeed = "4.09",
                visibility =  "10000",
                tempIcon = "02d"
            ),
            WeatherDTO(
                id = 2,
                temperature = "16",
                minTemperature = "15",
                maxTemperature = "18",
                description = "moderate rain",
                cloudiness = "83",
                humidity = "60",
                pressure = "1021",
                windSpeed = "4.09",
                visibility =  "10000",
                tempIcon = "04d"
            ),
            WeatherDTO(
                id = 3,
                temperature = "16",
                minTemperature = "15",
                maxTemperature = "18",
                description = "moderate rain",
                cloudiness = "83",
                humidity = "60",
                pressure = "1021",
                windSpeed = "4.09",
                visibility =  "10000",
                tempIcon = "03d"
            ),
            WeatherDTO(
                id = 4,
                temperature = "16",
                minTemperature = "15",
                maxTemperature = "18",
                description = "moderate rain",
                cloudiness = "83",
                humidity = "60",
                pressure = "1021",
                windSpeed = "4.09",
                visibility =  "10000",
                tempIcon = "50d"
            ),
            WeatherDTO(
                id = 5,
                temperature = "16",
                minTemperature = "15",
                maxTemperature = "18",
                description = "moderate rain",
                cloudiness = "83",
                humidity = "60",
                pressure = "1021",
                windSpeed = "4.09",
                visibility =  "10000",
                tempIcon = "09d"
            ),
        )*/

        binding.hourlyRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyAdapter
        }

        binding.dailyRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = dailyAdapter
        }

        val factory = HomeViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl.getInstance(
                    requireContext().applicationContext.cacheDir
                )
            ),
            requireActivity().application
        )
        Log.d(TAG, "onViewCreated: created")
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        subscribeToObservables()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: initiate")
        settings = requireActivity().getSharedPreferences(Constants.SharedPrefs.Settings.SETTINGS, MODE_PRIVATE)
        Constants.SharedPrefs.Settings.Location.Values.let {
            val location: String = settings.getString(LOCATION, it.GPS) ?: it.GPS
            if(location == it.GPS){
                Log.d(TAG, "onStart: GPS")
                if (checkLocationPermissions()) {
                    binding.apply {
                        permissionsDeniedGroup.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }
                    if (isLocationEnabled()) {
                        Log.d(TAG, "getFreshLocation: getting location...")
                        getFreshLocation()
                    } else {
                        Log.d(TAG, "getFreshLocation: asking for location service")
                        enableLocationServices()
                    }
                }else{
                    requestLocationPermissions()
                }
            }else if(location == it.MAP){
                Log.d(TAG, "onStart: Map")
                homeViewModel.setData(settings.getString(Constants.LATITUDE, "0")!!.toDouble(), settings.getString(
                    Constants.LONGITUDE, "0")!!.toDouble())
            }
        }
    }


/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult: ")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                if (isLocationEnabled()) {
                    Log.d(TAG, "onRequestPermissionsResult: getting fresh location...")
                    getFreshLocation()
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: requesting location services")
                    enableLocationServices()
                }
            }else{
                binding.apply {
                    permissionsDeniedGroup.visibility = View.VISIBLE
                    permissionsGrantedGroup.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }
            }
        }
    }*/

    @SuppressLint("SetTextI18n")
    private fun subscribeToObservables(){
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.weather.collectLatest { response->
                when(response){
                    is CurrentState.Failure -> {

                    }
                    is CurrentState.Loading -> {
                        handleLoadingState()
                    }
                    is CurrentState.Success -> {
                        handleSuccessState()
                        response.data.let {
                            binding.apply {
                                pressure.text = "${it.pressure} ${getString(R.string.hpa)}"
                                humidity.text = "${it.humidity}%"
                                wind.text = it.windSpeed
                                cloud.text = "${it.cloudiness}%"
                                visibility.text = it.visibility
                                date.text = it.date.toDateTime("EEE, dd MMM")
                                description.text = it.description
                                temperature.text = it.temperature
                                UV.text = it.ultraViolet
                                daytimeIcon.setImageResource(it.tempIcon.toDrawable2X())
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.forecastHourly.collectLatest { response ->
                Log.d(TAG, "subscribeToObservables: $response")
                when(response){
                    is CurrentState.Failure -> {

                    }
                    is CurrentState.Loading -> {
                        handleLoadingState()
                    }
                    is CurrentState.Success -> {
                        handleSuccessState()
                        hourlyAdapter.submitList(response.data.list)
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.forecastDaily.collectLatest { response ->
                Log.d(TAG, "subscribeToObservables: $response")
                when(response){
                    is CurrentState.Failure -> {

                    }
                    is CurrentState.Loading -> {
                        handleLoadingState()
                    }
                    is CurrentState.Success -> {
                        handleSuccessState()
                        dailyAdapter.submitList(response.data.list)
                    }
                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.city.collectLatest {
                Log.d(TAG, "subscribeToObservables: $it")
                binding.city.text = it
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.apply {
            permissionsDeniedGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }

        /*lifecycleScope.launch(Dispatchers.IO) {
            val locationTask = fusedLocationProviderClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).setMaxUpdateAgeMillis(
                    10
                ).build(),
                null
            )
            if(locationTask.isSuccessful){
                val latitude = locationTask.result.latitude
                val longitude = locationTask.result.longitude
                *//* openSmsBtn.apply {
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
                 }*//*
                Log.d(TAG, "onLocationResult: Longitude: $longitude, Latitude: $latitude")
                homeViewModel.setData(latitude = latitude, longitude = longitude)
            }else{
                Log.e(TAG, "getFreshLocation: ${if(locationTask.exception == null) locationTask.toString() else ""}", locationTask.exception)
            }
        }*/
            fusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.Builder(10).apply {
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
                        homeViewModel.setData(latitude = latitude, longitude = longitude)
                    }
                },
                Looper.myLooper()
            )
    }

    private fun checkLocationPermissions(): Boolean {
        return requireActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || requireActivity().checkSelfPermission(
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun enableLocationServices() {
        Toast.makeText(requireContext(), "turn on location", Toast.LENGTH_SHORT).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun requestLocationPermissions(){
        requestMultiplePermissions.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }





    companion object{
        private const val TAG = "HomeFragment"
    }

    private fun handleLoadingState(){
        binding.apply {
            if(::snackBar.isInitialized && snackBar.isShown){
                snackBar.dismiss()
            }
            permissionsGrantedGroup.visibility =  View.GONE
            permissionsDeniedGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun handleSuccessState(){
        binding.apply {
            permissionsGrantedGroup.visibility =  View.VISIBLE
            permissionsDeniedGroup.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun handleErrorState(message: String){
        snackBar = Snackbar.make(binding.root, message, Int.MAX_VALUE).apply {
            view.setBackgroundColor(requireContext().getColor(R.color.secondary))
        }
    }

}