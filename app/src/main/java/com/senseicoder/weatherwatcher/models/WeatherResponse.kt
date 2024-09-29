package com.senseicoder.weatherwatcher.models


import com.google.gson.annotations.SerializedName

/**
{
  "coord": {
    "lon": 7.367,
    "lat": 45.133
  },
  "weather": [
    {
      "id": 501,
      "main": "Rain",
      "description": "moderate rain",
      "icon": "10d"
    }
  ],
  "base": "stations",
  "main": {
    "temp": 284.2,
    "feels_like": 282.93,
    "temp_min": 283.06,
    "temp_max": 286.82,
    "pressure": 1021,
    "humidity": 60,
    "sea_level": 1021,
    "grnd_level": 910
  },
  "visibility": 10000,
  "wind": {
    "speed": 4.09,
    "deg": 121,
    "gust": 3.47
  },
  "rain": {
    "1h": 2.73
  },
  "clouds": {
    "all": 83
  },
  "dt": 1726660758,
  "sys": {
    "type": 1,
    "id": 6736,
    "country": "IT",
    "sunrise": 1726636384,
    "sunset": 1726680975
  },
  "timezone": 7200,
  "id": 3165523,
  "name": "Province of Turin",
  "cod": 200
}
*/
data class WeatherResponse(
    @SerializedName("base")
    val base: String?,
    @SerializedName("clouds")
    val clouds: Clouds?,
    @SerializedName("cod")
    val cod: Int?,
    @SerializedName("coord")
    val coord: Coord?,
    @SerializedName("dt")
    val dt: Long?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("main")
    val main: Main?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("rain")
    val rain: Rain?,
    @SerializedName("sys")
    val sys: Sys?,
    @SerializedName("timezone")
    val timezone: Int?,
    @SerializedName("visibility")
    val visibility: Int?,
    @SerializedName("weather")
    val weather: List<Weather?>?,
    @SerializedName("wind")
    val wind: Wind?
) {
    data class Clouds(
        @SerializedName("all")
        val all: Int?
    )

    data class Coord(
        @SerializedName("lat")
        val lat: Double?,
        @SerializedName("lon")
        val lon: Double?
    )

    data class Main(
        @SerializedName("feels_like")
        val feelsLike: Double?,
        @SerializedName("grnd_level")
        val grndLevel: Int?,
        @SerializedName("humidity")
        val humidity: Int?,
        @SerializedName("pressure")
        val pressure: Int?,
        @SerializedName("sea_level")
        val seaLevel: Int?,
        @SerializedName("temp")
        val temp: Double?,
        @SerializedName("temp_max")
        val tempMax: Double?,
        @SerializedName("temp_min")
        val tempMin: Double?
    )

    data class Rain(
        @SerializedName("1h")
        val h: Double?
    )

    data class Sys(
        @SerializedName("country")
        val country: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("sunrise")
        val sunrise: Int?,
        @SerializedName("sunset")
        val sunset: Int?,
        @SerializedName("type")
        val type: Int?
    )

    data class Weather(
        @SerializedName("description")
        val description: String?,
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("main")
        val main: String?
    )

    data class Wind(
        @SerializedName("deg")
        val deg: Int?,
        @SerializedName("gust")
        val gust: Double?,
        @SerializedName("speed")
        val speed: Double?
    )
}