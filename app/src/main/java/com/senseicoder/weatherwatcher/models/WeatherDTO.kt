package com.senseicoder.weatherwatcher.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore


/*
*
currentDate : none(from phone)
currentTime : none(from phone)
units (standard (which is kelvin), metric or imperial)
lang (either ar or en in request itself)
*
main {currentTemp : temp
    min : temp_min
    max : temp_max
    humidity : humidity
    pressure : pressure}
wind {windSpeed : speed}
weather{
    weatherDescription: description
    icon: icon
}
visibility: visibility
city: name (deprecated so use geocoder instead)
clouds{clouds : all}

all future hourly for current data
all future features 5
* */

@Entity(tableName = WeatherDTO.ENTITY_NAME, primaryKeys = [WeatherDTO.LOCATION, WeatherDTO.DATE])
data class WeatherDTO(
    @ColumnInfo(TEMP)
    var temperature: String,
    @ColumnInfo(MIN_TEMP)
    var minTemperature: String,
    @ColumnInfo(MAX_TEMP)
    var maxTemperature: String,
    @ColumnInfo(DESCRIPTION)
    var description: String,
    @ColumnInfo(ICON)
    var tempIcon: String,
    @ColumnInfo(PRESSURE)
    var pressure: String,
    @ColumnInfo(HUMIDITY)
    var humidity: String,
    @ColumnInfo(SPEED)
    var windSpeed: String,
    @ColumnInfo(ALL)
    var cloudiness: String,
    @ColumnInfo(ULTRA_VIOLET)
    var ultraViolet: String = "0",
    @ColumnInfo(VISIBILITY)
    var visibility: String,
    @ColumnInfo(DATE)
    var date: Long = 0,
    @ColumnInfo(LOCATION)
    var location: String = "",
){
    companion object Keys{
        fun fromWeatherResponse(res: WeatherResponse) : WeatherDTO{
            return WeatherDTO(
                (res.main?.temp ?: "unknown").toString(),
                (res.main?.tempMin ?: "unknown").toString(),
                (res.main?.tempMax ?: "unknown").toString(),
                (res.weather?.get(0)?.description ?: "unknown").toString(),
                (res.weather?.get(0)?.icon ?: "unknown").toString(),
                (res.main?.pressure ?: "unknown").toString(),
                (res.main?.humidity ?: "unknown").toString(),
                (res.wind?.speed ?: "unknown").toString(),
                (res.clouds?.all ?: "unknown").toString(),
                "0",
                (res.visibility ?: "unknown").toString(),
                (res.dt ?: 0),
                location = (res.name ?: ""),
            )
        }

        const val ENTITY_NAME = "current_weather"
        const val WEATHER = "Weather"
        const val ID = "id"
        const val TEMP = "temp"
        const val MIN_TEMP = "min_temp"
        const val MAX_TEMP = "max_temp"
        const val DESCRIPTION = "description"
        const val ICON = "icon"
        const val PRESSURE = "pressure"
        const val HUMIDITY = "humidity"
        const val SPEED = "speed"
        const val ALL = "all"
        const val ULTRA_VIOLET  = "ultraViolet" //unknown yet
        const val VISIBILITY = "visibility"
        const val UNITS = "units"
        const val DATE = "dt"
        const val DATE_TEXT = "dt_txt"
        const val LIST = "list"
        const val TIME_ZONE = "timezone"
        const val LOCATION = "location"
    }
}