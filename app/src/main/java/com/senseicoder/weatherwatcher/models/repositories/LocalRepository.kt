package com.senseicoder.weatherwatcher.models.repositories

import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    suspend fun getAlerts() : Flow<List<AlertDTO>>
    suspend fun insertAlert(alertDTO: AlertDTO): Long
    suspend fun deleteAlert(alertId: Long): Int

    suspend fun getFavorites() : Flow<List<FavoriteDTO>>
    suspend fun insertFavorites(favoriteDTO: FavoriteDTO): Long
    suspend fun deleteFavorites(location: String): Int
}