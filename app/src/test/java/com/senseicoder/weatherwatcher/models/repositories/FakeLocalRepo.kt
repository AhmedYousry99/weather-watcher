package com.senseicoder.weatherwatcher.models.repositories

import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalRepo: LocalRepository  {

      val alerts = mutableListOf<AlertDTO>()
      val favorites = mutableListOf<FavoriteDTO>()


    override suspend fun getAlerts(): Flow<List<AlertDTO>> {
        return flow{
            emit(alerts)
        }
    }

    override suspend fun insertAlert(alertDTO: AlertDTO): Long {
        alerts.add(alertDTO)
        return 1
    }

    override suspend fun deleteAlert(alertId: Long): Int {
        alerts.remove(alerts.find { it.id == alertId })
        return 1
    }

    override suspend fun getFavorites(): Flow<List<FavoriteDTO>> {
        return flow{
            emit(favorites)
        }
    }

    override suspend fun insertFavorites(favoriteDTO: FavoriteDTO): Long {
        val item = favorites.find { favoriteDTO.location == it.location }
        if(item != null){
            favorites.remove(item)
        }
        favorites.add(favoriteDTO)
        return 1
    }

    override suspend fun deleteFavorites(location: String): Int {
        val item = favorites.find { it.location == location}
        if(item != null){
            favorites.remove(item)

        }
        return 1
    }
}