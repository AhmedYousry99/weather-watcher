package com.senseicoder.weatherwatcher.models.repositories

import com.senseicoder.weatherwatcher.models.CitiesResponse
import com.senseicoder.weatherwatcher.models.ForecastResponse
import com.senseicoder.weatherwatcher.models.ReverseGeocodeResponse
import com.senseicoder.weatherwatcher.models.UVResponse
import com.senseicoder.weatherwatcher.models.WeatherResponse
import retrofit2.Response

interface WeatherRepository {
    suspend fun getWeatherToday(longitude: Double, latitude: Double) : Response<WeatherResponse>
    suspend fun getWeatherForecast(longitude: Double, latitude: Double) : Response<ForecastResponse>
    suspend fun getUVToday(longitude: Double, latitude: Double) : Response<UVResponse>
    suspend fun getLocationDescription(longitude: Double, latitude: Double) : Response<ReverseGeocodeResponse>
    suspend fun getCity(query: String) : Response<CitiesResponse>
}