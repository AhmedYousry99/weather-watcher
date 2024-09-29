package com.senseicoder.weatherwatcher.features.map

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.databinding.ActivityMapBinding
import com.senseicoder.weatherwatcher.features.map.viewmodels.MapViewModel
import com.senseicoder.weatherwatcher.features.map.viewmodels.MapViewModelFactory
import com.senseicoder.weatherwatcher.models.CitiesResponse
import com.senseicoder.weatherwatcher.models.ReverseGeocodeDTO
import com.senseicoder.weatherwatcher.models.repositories.WeatherRepositoryImpl
import com.senseicoder.weatherwatcher.network.RemoteDataSourceImpl
import com.senseicoder.weatherwatcher.utils.dialogs.LocationDialogFragment
import com.senseicoder.weatherwatcher.utils.global.Constants
import com.senseicoder.weatherwatcher.utils.global.showSnackbar
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapActivity : AppCompatActivity() {

    lateinit var binding: ActivityMapBinding
    lateinit var mapViewModel: MapViewModel

    private lateinit var controller: IMapController
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    private lateinit var firstMarker: Marker

    private lateinit var locationDialogFragment: LocationDialogFragment

    private lateinit var settings: SharedPreferences

    private var longitude: Double? = null
    private var latitude: Double? = null

    private  var queryFlow: MutableSharedFlow<String> = MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private lateinit var suggestionAdapter: ArrayAdapter<CitiesResponse.CityResponseItem>

    companion object{
        private const val TAG = "MapActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapBinding.inflate(layoutInflater)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = Slide(Gravity.START)
        }
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.root.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val factory = MapViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                remoteDataSource = RemoteDataSourceImpl.getInstance(application.cacheDir)
            ),
            application
        )
        mapViewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        settings = getSharedPreferences(Constants.SharedPrefs.Settings.SETTINGS, MODE_PRIVATE)

        setupMapView()
        setupSearchView()
        setupListeners()
        subscribeToObservables()
    }

    private fun setupSearchView() {
        binding.mapSearchView.apply {
            clearFocus()
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(newText: String?): Boolean {
                    clearFocus()
                    binding.suggestionsListView.visibility = View.GONE
                    return true
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d(TAG, "onQueryTextChange: $newText")
//                            mealDoesntExitTextView.setVisibility(View.INVISIBLE)
                    if(newText.isNullOrBlank() || newText.trim().length < 2){
                        binding.suggestionsListView.visibility = View.GONE
                        lifecycleScope.launch(Dispatchers.IO){
                            queryFlow.emit("")
                        }
                    }else{
                        lifecycleScope.launch(Dispatchers.IO){
                            queryFlow.emit(newText.trim())
                        }
                    }
                    return true
                }
            })
        }
    }

    private fun setupListeners(){
        binding.apply {
            lifecycleScope.launch {
                queryFlow.debounce(1000).collect {
                    Log.d(TAG, "setupListeners: $it")
                    if(it.isNotBlank()){
                        mapViewModel.getCities(it)
                    }
                }
            }
            mapSearchView.setOnQueryTextFocusChangeListener { _: View?, hasFocus: Boolean ->
                if (hasFocus) suggestionsListView.visibility = View.VISIBLE
                else suggestionsListView.visibility = View.GONE
            }
            suggestionsListView.onItemClickListener =
                OnItemClickListener { parent, _, position, _ ->
                    val suggestion = parent.getItemAtPosition(position) as CitiesResponse.CityResponseItem
                    mapSearchView.setQuery(suggestion.name, true)
                    mapSearchView.clearFocus()
                    Log.d(TAG, "setupListeners: $suggestion")
                    suggestionsListView.visibility = View.GONE
                    controller.animateTo(GeoPoint(suggestion.latitude!!, suggestion.longitude!!))
                    controller.setZoom(10.0)
                    /*LocationDialogFragment(
                        suggestion
                    ){
                        val intent = Intent()
                        intent.putExtra(
                            "language", if (
                                settings.getString(
                                    Constants.SharedPrefs.Settings.Language.LANGUAGE,
                                    ""
                                ) == Constants.SharedPrefs.Settings.Language.Values.AR
                            ) suggestion.name else suggestion.name
                        )
                        intent.putExtra(
                            Constants.LATITUDE, suggestion.latitude
                        )
                        intent.putExtra(
                            Constants.LONGITUDE, suggestion.longitude
                        )
                        setResult(RESULT_OK, intent)
                        finish()
                    }.show(supportFragmentManager, null)*/
                }
        }
    }

    private fun subscribeToObservables(){
        lifecycleScope.launch {
            mapViewModel.cities.collectLatest {
                response ->
                when(response){
                    is CurrentState.Failure -> {
                        binding.root.showSnackbar(
                            response.msg,
                            3000
                        )
                    }
                    is CurrentState.Loading -> {

                    }
                    is CurrentState.Success -> {
                        binding.suggestionsListView.visibility = View.VISIBLE
                        Log.d(TAG, "subscribeToObservables: ${response.data}")
                        suggestionAdapter = ArrayAdapter(
                            this@MapActivity,
                            android.R.layout.simple_list_item_1,
                            response.data
                        )
                        binding.suggestionsListView.adapter = suggestionAdapter
                    }
                }
            }
        }
    }

    private fun setupMapView() {
        binding.mapView.let { mapView ->
            mapView.setMultiTouchControls(true)
            mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
            mLocationOverlay.enableMyLocation()
            mapView.overlays.add(mLocationOverlay)
            controller = mapView.controller.apply {
                setZoom(5.0)
            }
            val mapEventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(point: GeoPoint?): Boolean {
                    Log.d(TAG, "singleTapConfirmedHelper: called")
                    point.run {
                        latitude = this?.latitude
                        longitude = this?.longitude
                        if (latitude != null && longitude != null) lifecycleScope.launch(Dispatchers.IO) {
                            launch(Dispatchers.IO) {
                                mapViewModel.getCityData(
                                    longitude!!, latitude!!
                                )
                            }.join()
                            launch(Dispatchers.Main) {
                                mapViewModel.location.collectLatest { res ->
                                    if (res is CurrentState.Success<ReverseGeocodeDTO>) {
                                        updateMarker(GeoPoint(
                                            latitude!!,
                                            longitude!!,
                                        ),
                                            if (
                                                settings.getString(
                                                    Constants.SharedPrefs.Settings.Language.LANGUAGE,
                                                    "en"
                                                ) == Constants.SharedPrefs.Settings.Language.Values.AR
                                            ) res.data.arabic else res.data.english)
                                        showFragmentDialog(res.data)
                                    } else if(res is CurrentState.Failure){
                                        binding.root.showSnackbar(
                                            res.msg,
                                            5000
                                        )
                                    }
                                }
                            }
                        }
                    }
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean {
                    return true
                }
            }
            val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
            mapView.overlays.add(mapEventsOverlay)

            onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.run{
                        if(mapSearchView.hasFocus()){
                            mapSearchView.clearFocus()
                            suggestionsListView.visibility = View.GONE
                        }else{
                            remove()
                            setResult(RESULT_CANCELED, Intent())
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }

            })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    private fun showFragmentDialog(data: ReverseGeocodeDTO) {
        if(::locationDialogFragment.isInitialized){
            locationDialogFragment.dismiss()
        }
        locationDialogFragment =LocationDialogFragment(
            data.run{
                CitiesResponse.CityResponseItem(
                    country,
                    false,
                    latitude,
                    longitude,
                    if (
                        settings.getString(
                            Constants.SharedPrefs.Settings.Language.LANGUAGE,
                            ""
                        ) == Constants.SharedPrefs.Settings.Language.Values.AR
                    ) arabic else english,
                    0
                )
            }
        ){
            val intent = Intent()
            locationDialogFragment.location.also{
                intent.putExtra(
                    "${Constants.PACKAGE_NAME}.${Constants.NAME}", it.name)
                intent.putExtra(
                    "${Constants.PACKAGE_NAME}.${Constants.LATITUDE}", it.latitude.toString()
                )
                intent.putExtra(
                    "${Constants.PACKAGE_NAME}.${Constants.LONGITUDE}", it.longitude.toString()
                )
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        locationDialogFragment.show(supportFragmentManager, null)
    }

    fun updateMarker(geoPoint: GeoPoint, title: String?) {
        if (!::firstMarker.isInitialized) {
            firstMarker = Marker(binding.mapView)
            firstMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
            firstMarker.title = title ?: getString(R.string.you_clicked_here)
            binding.mapView.overlays.add(firstMarker)
        }
        controller.animateTo(geoPoint)
        firstMarker.position = geoPoint
        binding.mapView.invalidate()
    }

}