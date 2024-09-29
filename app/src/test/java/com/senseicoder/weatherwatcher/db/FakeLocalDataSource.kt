package com.senseicoder.weatherwatcher.db

import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import com.senseicoder.weatherwatcher.models.WeatherDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource(private var alerts: MutableList<AlertDTO> = mutableListOf()): LocalDataSource {

    override suspend fun getCurrentWeatherData(location: String): Flow<List<WeatherDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeatherData(weatherDTO: WeatherDTO): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getAlerts(): Flow<List<AlertDTO>> {
        return flow{
            alerts
        }
    }

    override suspend fun insertAlert(alertDTO: AlertDTO): Long {
        alerts.add(alertDTO)
        return alertDTO.id
    }

    override suspend fun deleteAlert(alertId: Long): Int {
        val item = alerts.find { it.id == alertId }
        if(item != null){
            alerts.remove(item)
        }
        return alertId.toInt()
    }

    override fun getFavorites(): Flow<List<FavoriteDTO>> {
        TODO("Not yet implemented")
    }

    override fun insertFavorite(favoriteDTO: FavoriteDTO): Long {
        TODO("Not yet implemented")
    }

    override fun deleteFavorite(location: String): Int {
        TODO("Not yet implemented")
    }
}