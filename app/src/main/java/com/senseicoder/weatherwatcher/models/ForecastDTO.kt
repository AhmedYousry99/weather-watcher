package com.senseicoder.weatherwatcher.models

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson


@Entity(tableName = ForecastDTO.ENTITY_NAME)
@TypeConverters(WeatherDTOTypeConverter::class)
data class ForecastDTO(
    @ColumnInfo(COUNT)
    var count: Int,
    @PrimaryKey
    var city: String = "",

    var list: List<WeatherDTO>
) {
    companion object Keys {
        fun fromForecastResponse(res: ForecastResponse): ForecastDTO {
            return ForecastDTO(
                count = (res.cnt ?: 0),
                city = (res.city?.name ?: ""),
                list = res.list?.map { WeatherDTO(
                    temperature = (it?.main?.temp ?: "unknown").toString(),
                    minTemperature = (it?.main?.tempMin ?: "unknown").toString(),
                    maxTemperature = (it?.main?.tempMax ?: "unknown").toString(),
                    description = (it?.weather?.get(0)?.description ?: "unknown").toString(),
                    tempIcon = (it?.weather?.get(0)?.icon ?: "unknown").toString(),
                    cloudiness = (it?.clouds?.all ?: "unknown").toString(),
                    pressure = (it?.main?.pressure ?: "unknown").toString(),
                    humidity = (it?.main?.humidity ?: "unknown").toString(),
                    visibility = (it?.visibility ?: "unknown").toString(),
                    windSpeed = (it?.main?.tempMax ?: "unknown").toString(),
                    date = (it?.dt ?: 0),
                ) }?.toList() ?: emptyList(),
            )
        }

        const val FORECAST = "forecast"
        const val ENTITY_NAME = "forecast"
        const val ID = "id"
        const val WEATHER_ID = "wId"
        const val TEMP = "temp"
        const val MIN_TEMP = "min_temp"
        const val MAX_TEMP = "max_temp"
        const val DESCRIPTION = "description"
        const val ICON = "icon"
        const val PRESSURE = "pressure"
        const val HUMIDITY = "humidity"
        const val SPEED = "speed"
        const val ALL = "all"
        const val ULTRA_VIOLET = "ultraViolet" //unknown yet
        const val VISIBILITY = "visibility"
        const val UNITS = "units"
        const val DATE = "dt"
        const val DATE_TEXT = "dt_txt"
        const val LIST = "list"
        const val TIME_ZONE = "timezone"
        const val CITY = "city"
        const val COUNT = "cnt"
    }

}


class WeatherDTOTypeConverter {
    @TypeConverter
    fun fromArrayList(weathers: List<WeatherDTO>): String {
        return Gson().toJson(weathers)
    }

    @TypeConverter
    fun toArrayList(json: String): List<WeatherDTO>{
        Log.d(TAG, "toArrayList: $json")
        return Gson().fromJson(json, Array<WeatherDTO>::class.java).toList()
    }

    companion object{
        private const val TAG = "ForecastDTO"
    }

}