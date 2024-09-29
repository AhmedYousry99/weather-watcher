package com.senseicoder.weatherwatcher.features.map.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.weatherwatcher.R
import com.senseicoder.weatherwatcher.models.CitiesResponse
import com.senseicoder.weatherwatcher.models.ReverseGeocodeDTO
import com.senseicoder.weatherwatcher.models.repositories.WeatherRepository
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(private val repository: WeatherRepository, private val application: Application): ViewModel() {

    private val _location = MutableSharedFlow<CurrentState<ReverseGeocodeDTO>>(extraBufferCapacity = 1,onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val location = _location.asSharedFlow()

    private val _cities = MutableSharedFlow<CurrentState<List<CitiesResponse.CityResponseItem>>>(extraBufferCapacity = 1,onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val cities = _cities.asSharedFlow()


    fun getCityData(longitude: Double, latitude: Double){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getLocationDescription(
                longitude, latitude
            ).run {
                if(isSuccessful){
                    val data = body()
                    if(!data.isNullOrEmpty() && (!data[0].name.isNullOrBlank() || (!data[0].localNames?.ar.isNullOrBlank() && !data[0].localNames?.en.isNullOrBlank()))){
                        Log.d(TAG, "getCityData: $data")
                        val dto = ReverseGeocodeDTO.fromReverseGeocodeResponse(data)
                        withContext(Dispatchers.Main){
                            _location.emit(CurrentState.Success(dto))
                        }
                    }else{
                        Log.e(TAG, "getCityData: $this")
                        _location.emit(CurrentState.Failure(application.getString(R.string.couldnt_get_location_name)))
                    }
                }else{
                    Log.e(TAG, "getCityData: $this")
                    _location.emit(CurrentState.Failure(application.getString(R.string.couldnt_get_location_name)))
                }
            }
        }
    }

    fun getCities(query: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCity(
                query
            ).run {
                if(isSuccessful){
                    val data = body()
                    if(data != null){
                        _cities.emit(CurrentState.Success(data.filter { !(it.name.isNullOrBlank())}.distinctBy {
                            it.country
                        }))
                    }else{
                        Log.e(TAG, "getCities: $this")
                        _cities.emit(CurrentState.Failure(application.getString(R.string.couldnt_get_cities)))
                    }
                }else{
                    Log.e(TAG, "getCities: $this")
                    _cities.emit(CurrentState.Failure(application.getString(R.string.couldnt_get_cities)))
                }
            }
        }
    }

    companion object{
        private const val TAG = "MapViewModel"
    }
}