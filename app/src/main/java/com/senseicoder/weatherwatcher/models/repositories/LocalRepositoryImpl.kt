package com.senseicoder.weatherwatcher.models.repositories

import com.senseicoder.weatherwatcher.db.LocalDataSource
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import kotlinx.coroutines.flow.Flow

class LocalRepositoryImpl private constructor(private val localDataSource: LocalDataSource): LocalRepository {

    override suspend fun getAlerts(): Flow<List<AlertDTO>> {
        return localDataSource.getAlerts()
    }

    override suspend fun insertAlert(alertDTO: AlertDTO): Long {
        return localDataSource.insertAlert(alertDTO)
    }

    override suspend fun deleteAlert(alertId: Long): Int {
        return localDataSource.deleteAlert(alertId)
    }

    override suspend fun getFavorites(): Flow<List<FavoriteDTO>> {
        return localDataSource.getFavorites()
    }

    override suspend fun insertFavorites(favoriteDTO: FavoriteDTO): Long {
        return localDataSource.insertFavorite(favoriteDTO)
    }

    override suspend fun deleteFavorites(location: String): Int {
        return localDataSource.deleteFavorite(location)
    }

    companion object {
        private var repo: LocalRepositoryImpl? = null

        fun getInstance(localDataSource: LocalDataSource): LocalRepositoryImpl {
            if (repo == null) repo = LocalRepositoryImpl(localDataSource)
            return repo!!
        }
    }
}