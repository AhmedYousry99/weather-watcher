package com.senseicoder.weatherwatcher.features.drawer.favorites.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.weatherwatcher.models.repositories.LocalRepository

@Suppress("UNCHECKED_CAST")
class FavoriteViewModelFactory (private val localRepository: LocalRepository, val application: Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(localRepository, application) as T
        }else{
            throw IllegalArgumentException("couldn't create object from model class: ${modelClass.name}")
        }
    }
}