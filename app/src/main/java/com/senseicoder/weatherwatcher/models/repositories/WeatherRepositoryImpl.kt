package com.senseicoder.weatherwatcher.models.repositories

import com.senseicoder.weatherwatcher.models.CitiesResponse
import com.senseicoder.weatherwatcher.models.ForecastResponse
import com.senseicoder.weatherwatcher.models.ReverseGeocodeResponse
import com.senseicoder.weatherwatcher.models.UVResponse
import com.senseicoder.weatherwatcher.models.WeatherResponse
import com.senseicoder.weatherwatcher.network.RemoteDataSource
import retrofit2.Response

class WeatherRepositoryImpl private constructor(val remoteDataSource: RemoteDataSource): WeatherRepository {

    companion object {
        const val TAG: String = "WeatherRepositoryImpl"
        @Volatile
        private var instance: WeatherRepositoryImpl? = null
        fun getInstance(remoteDataSource: RemoteDataSource): WeatherRepositoryImpl {
            return instance ?: synchronized(this){
                val instance =
                    WeatherRepositoryImpl(remoteDataSource)
                this.instance = instance
                instance
            }
        }
    }


    override suspend fun getWeatherToday(
        longitude: Double,
        latitude: Double
    ): Response<WeatherResponse> {
        return remoteDataSource.getWeatherToday(longitude = longitude, latitude = latitude)
    }

    override suspend fun getWeatherForecast(
        longitude: Double,
        latitude: Double
    ): Response<ForecastResponse> {
        return remoteDataSource.getWeatherForecast(longitude = longitude, latitude = latitude)
    }

    override suspend fun getUVToday(longitude: Double, latitude: Double): Response<UVResponse> {
        return remoteDataSource.getUVToday(longitude, latitude)
    }

    override suspend fun getLocationDescription(
        longitude: Double,
        latitude: Double
    ): Response<ReverseGeocodeResponse> {
        return remoteDataSource.getReverseGeocode(longitude = longitude, latitude = latitude)
    }

    override suspend fun getCity(query: String): Response<CitiesResponse> {
        return remoteDataSource.getCities(query)
    }
}