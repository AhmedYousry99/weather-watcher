package com.senseicoder.weatherwatcher.features.map.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.weatherwatcher.models.repositories.WeatherRepository

@Suppress("UNCHECKED_CAST")
class MapViewModelFactory(private val repository: WeatherRepository, private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            MapViewModel(repository, this.application) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}