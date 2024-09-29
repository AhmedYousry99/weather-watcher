package com.senseicoder.weatherwatcher.features.drawer.alerts.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.weatherwatcher.models.repositories.LocalRepository

@Suppress("UNCHECKED_CAST")
class AlertViewModelFactory(private val localRepository: LocalRepository, val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(localRepository, application) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}