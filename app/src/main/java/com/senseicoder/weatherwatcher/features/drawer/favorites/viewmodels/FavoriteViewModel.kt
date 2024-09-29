package com.senseicoder.weatherwatcher.features.drawer.favorites.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import com.senseicoder.weatherwatcher.models.repositories.LocalRepository
import com.senseicoder.weatherwatcher.utils.wrappers.CurrentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val localRepository: LocalRepository, val application: Application): ViewModel()  {
    private val _favorites = MutableStateFlow<CurrentState<List<FavoriteDTO>>>(CurrentState.Loading())
    val favorites = _favorites.asStateFlow()

    init {
        getFavorites()
    }

    fun getFavorites(){
        viewModelScope.launch(Dispatchers.IO){
            localRepository.getFavorites().collect{
                _favorites.value = CurrentState.Success(it)
            }
        }
    }

    fun insertFavorite(favoriteDTO: FavoriteDTO){
        viewModelScope.launch(Dispatchers.IO){
            localRepository.insertFavorites(favoriteDTO)
        }
    }

    fun deleteFavorite(location: String){
        viewModelScope.launch(Dispatchers.IO){
            localRepository.deleteFavorites(location)
        }
    }
}