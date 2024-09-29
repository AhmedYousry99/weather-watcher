package com.senseicoder.weatherwatcher.features.drawer.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.models.ForecastDTO
import com.senseicoder.weatherwatcher.models.ReverseGeocodeDTO
import com.senseicoder.weatherwatcher.models.WeatherDTO
import com.senseicoder.weatherwatcher.models.repositories.WeatherRepository
import com.senseicoder.weatherwatcher.utils.global.toDateTime
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(private val repository: WeatherRepository,
                    private val application: Application) : ViewModel() {

    private val _weather: MutableStateFlow<CurrentState<WeatherDTO>> = MutableStateFlow(
        CurrentState.Loading()
    )

    val weather = _weather.asStateFlow()

    private val _forecastHourly: MutableStateFlow<CurrentState<ForecastDTO>> =
        MutableStateFlow(CurrentState.Loading())

    val forecastHourly = _forecastHourly.asStateFlow()

    private val _forecastDaily: MutableStateFlow<CurrentState<ForecastDTO>> =
        MutableStateFlow(CurrentState.Loading())

    val forecastDaily = _forecastDaily.asStateFlow()

    private val _city: MutableStateFlow<String> =
        MutableStateFlow("")

    val city = _city.asStateFlow()


    fun setData(latitude: Double, longitude: Double){
        getTodayWeatherData(latitude = latitude, longitude = longitude)

        getWeatherForecast(latitude = latitude, longitude = longitude)

        getLocationDescription(latitude = latitude, longitude = longitude)
    }
    

    private fun getWeatherForecast(latitude: Double, longitude: Double) : Job{
        return viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherForecast(latitude, longitude).apply {
                if (isSuccessful) {
                    val res = body()
                    if (res == null || body()?.cod != "200") {
                        Log.e(TAG, "something went wrong...\n${toString()}")
                        _forecastHourly.value = CurrentState.Failure(application.getString(R.string.something_went_wrong))
                    } else {
                        Log.d(TAG, "working: ${res.list?.size}")
                        val forecastHourly = ForecastDTO.fromForecastResponse(res)
                        val forecastDaily = getDaysList(forecastHourly)
                        withContext(Dispatchers.Main) {
                            _forecastHourly.value = CurrentState.Success(forecastHourly)
                            _forecastDaily.value = CurrentState.Success(forecastDaily)
                        }
                    }
                } else {
                    Log.e(TAG, "retrofit error: " + toString())
                    _forecastHourly.value = CurrentState.Failure(application.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun getTodayWeatherData(latitude: Double, longitude: Double): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            val uvJob = async (Dispatchers.IO){
                var pair : Pair<Double, Double>? = null
                repository.getUVToday(latitude, longitude).apply {
                    if (isSuccessful) {
                        val res = body()
                        if (res == null) {
                            Log.e(TAG, "something went wrong...\n${toString()}", null)
                        } else {
                            res.result.run {
                                pair = Pair(this?.uv ?: -1.0, this?.uvMax ?: -1.0)
                            }
                        }
                    } else {
                        Log.e(TAG, "retrofit error: " + toString(), null)
                    }
                }
                pair
            }

            launch (Dispatchers.IO){
                repository.getWeatherToday(latitude, longitude).apply {
                    if (isSuccessful) {
                        val res = body()
                        if (res == null || body()?.cod != 200) {
                            Log.e(TAG, "something went wrong...\n${toString()}", null)
                            _weather.value = CurrentState.Failure(application.getString(R.string.something_went_wrong))
                        } else {
                            val weather = WeatherDTO.fromWeatherResponse(res)
                            Log.d(TAG, "getTodayWeatherData: $weather")
                            val uvResult = uvJob.await()
                            withContext(Dispatchers.Main) {
                                Log.d(TAG, ": $uvResult")
                                _weather.value = CurrentState.Success(
                                    weather.apply {
                                        Log.d(TAG, ": $uvResult")
                                        if(uvResult?.first != -1.0 )
                                            ultraViolet = if(uvResult?.first != uvResult?.second){
                                                "${uvResult?.first} ${application.getString(R.string.to)} ${uvResult?.second}"
                                            }else{
                                                "${uvResult?.first}"
                                            }
                                    }
                                )
                            }
                        }
                    } else {
                        Log.e(TAG, "retrofit error: " + toString())
                        _weather.value = CurrentState.Failure(application.getString(R.string.something_went_wrong))
                    }
                }
            }
        }
    }

    private fun getDaysList(it: ForecastDTO): ForecastDTO {
        var min: WeatherDTO
        var max: WeatherDTO
        return if (it.list.isEmpty()) {
            it
        } else {
            val tempList = it.list.fold(ArrayList<ArrayList<WeatherDTO>>()) { list, item ->
                list.apply {
                    if (isEmpty() || last().last().date.toDateTime("EEEE") != item.date.toDateTime("EEEE")) {
                        add(arrayListOf(item))
                    }
                    else {
                        last().add(item)
                    }
                }
            }
            val result = mutableListOf<WeatherDTO>()
            for(list in tempList){
                min = list.minBy { it.minTemperature }
                max = list.maxBy { it.maxTemperature }
                result.add(max.copy(minTemperature = min.minTemperature))
            }
            it.copy(count = result.count(), list = result)
        }
    }
    private fun getLocationDescription(latitude: Double, longitude: Double){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLocationDescription(longitude =longitude, latitude = latitude).apply {
                if (isSuccessful) {
                    val res = body()
                    if (res == null) {
                        Log.e(TAG, "something went wrong...\n${toString()}", null)
                    } else {
                        ReverseGeocodeDTO.fromReverseGeocodeResponse(res).let {
                            _city.value = it.english
                        }
                    }
                } else {
                    _city.value = this@HomeViewModel.application.getString(R.string.location_unknown)
                }
            }
        }
    }
    /*private fun getLocationDescription(latitude: Double, longitude: Double, context :Context = application) {
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 5, object: Geocoder.GeocodeListener{
                    override fun onGeocode(addresses: MutableList<Address>) {
                        Log.i(TAG, "onGeocode: $addresses , actual : $latitude , $longitude")
                        val address = addresses[0]
                        val addressStringBuilder = StringBuilder()
                        for (i in 0..address.maxAddressLineIndex) {
                            Log.i(TAG, "updateDescriptionText: ${address.getAddressLine(i)}")
                            addressStringBuilder.append(address.getAddressLine(i)).append("\n")
                        }
                        _city.value = addressStringBuilder.toString()
                    }
                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        Log.e(TAG, "onError: Error getting address: $errorMessage")
                    }
                })
            } else {
                try {
                    var addresses: MutableList<Address>? =
                        geocoder.getFromLocation(latitude, longitude, 1)
                    Log.d(TAG, "getLocationDescription: ${addresses.toString()}")
                    val address = addresses?.get(0)
                    val addressStringBuilder = StringBuilder()
                    if(address != null){
                        for (i in 0..address.maxAddressLineIndex) {
                            Log.i(TAG, "updateDescriptionText: ${address.getAddressLine(i)}")
                            addressStringBuilder.append(address.getAddressLine(i)).append("\n")
                        }
                    }else{
                        _city.value = this@HomeViewModel.application.getString(R.string.location_unknown)
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "getLocationDescription: ", e)
                }
            }
        }
    }*/


    companion object {
        private const val TAG = "HomeViewModel"
    }


}