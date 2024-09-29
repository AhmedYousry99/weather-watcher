package com.senseicoder.weatherwatcher.models


import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
{
  "cod": "200",
  "message": 0,
  "cnt": 40,
  "list": [
    {
      "dt": 1726898400,
      "main": {
        "temp": 284.7,
        "feels_like": 284.31,
        "temp_min": 284.7,
        "temp_max": 287.01,
        "pressure": 1022,
        "sea_level": 1022,
        "grnd_level": 954,
        "humidity": 92,
        "temp_kf": -2.31
      },
      "weather": [
        {
          "id": 803,
          "main": "Clouds",
          "description": "broken clouds",
          "icon": "04d"
        }
      ],
      "clouds": {
        "all": 77
      },
      "wind": {
        "speed": 0.94,
        "deg": 277,
        "gust": 1.1
      },
      "visibility": 10000,
      "pop": 0,
      "sys": {
        "pod": "d"
      },
      "dt_txt": "2024-09-21 06:00:00"
    },
    {
      "dt": 1727287200,
      "main": {
        "temp": 289.36,
        "feels_like": 289.15,
        "temp_min": 289.36,
        "temp_max": 289.36,
        "pressure": 1010,
        "sea_level": 1010,
        "grnd_level": 943,
        "humidity": 81,
        "temp_kf": 0
      },
      "weather": [
        {
          "id": 804,
          "main": "Clouds",
          "description": "overcast clouds",
          "icon": "04n"
        }
      ],
      "clouds": {
        "all": 100
      },
      "wind": {
        "speed": 2.45,
        "deg": 199,
        "gust": 5.28
      },
      "visibility": 10000,
      "pop": 0.03,
      "sys": {
        "pod": "n"
      },
      "dt_txt": "2024-09-25 18:00:00"
    },
    {
      "dt": 1727298000,
      "main": {
        "temp": 288.96,
        "feels_like": 289,
        "temp_min": 288.96,
        "temp_max": 288.96,
        "pressure": 1010,
        "sea_level": 1010,
        "grnd_level": 943,
        "humidity": 92,
        "temp_kf": 0
      },
      "weather": [
        {
          "id": 500,
          "main": "Rain",
          "description": "light rain",
          "icon": "10n"
        }
      ],
      "clouds": {
        "all": 100
      },
      "wind": {
        "speed": 2.36,
        "deg": 176,
        "gust": 3.97
      },
      "visibility": 10000,
      "pop": 0.78,
      "rain": {
        "3h": 0.84
      },
      "sys": {
        "pod": "n"
      },
      "dt_txt": "2024-09-25 21:00:00"
    },
    {
      "dt": 1727308800,
      "main": {
        "temp": 289.73,
        "feels_like": 289.87,
        "temp_min": 289.73,
        "temp_max": 289.73,
        "pressure": 1008,
        "sea_level": 1008,
        "grnd_level": 942,
        "humidity": 93,
        "temp_kf": 0
      },
      "weather": [
        {
          "id": 500,
          "main": "Rain",
          "description": "light rain",
          "icon": "10n"
        }
      ],
      "clouds": {
        "all": 100
      },
      "wind": {
        "speed": 2.69,
        "deg": 203,
        "gust": 5.06
      },
      "visibility": 10000,
      "pop": 0.87,
      "rain": {
        "3h": 0.96
      },
      "sys": {
        "pod": "n"
      },
      "dt_txt": "2024-09-26 00:00:00"
    },
    {
      "dt": 1727319600,
      "main": {
        "temp": 289.41,
        "feels_like": 289.49,
        "temp_min": 289.41,
        "temp_max": 289.41,
        "pressure": 1007,
        "sea_level": 1007,
        "grnd_level": 940,
        "humidity": 92,
        "temp_kf": 0
      },
      "weather": [
        {
          "id": 500,
          "main": "Rain",
          "description": "light rain",
          "icon": "10n"
        }
      ],
      "clouds": {
        "all": 100
      },
      "wind": {
        "speed": 1.8,
        "deg": 198,
        "gust": 4.38
      },
      "visibility": 10000,
      "pop": 0.27,
      "rain": {
        "3h": 0.27
      },
      "sys": {
        "pod": "n"
      },
      "dt_txt": "2024-09-26 03:00:00"
    }
  ],
  "city": {
    "id": 3163858,
    "name": "Zocca",
    "coord": {
      "lat": 44.34,
      "lon": 10.99
    },
    "country": "IT",
    "population": 4593,
    "timezone": 7200,
    "sunrise": 1726894936,
    "sunset": 1726938951
  }
}
*/
data class ForecastResponse(
    @SerializedName("city")
    val city: City?,
    @SerializedName("cnt")
    val cnt: Int?,
    @SerializedName("cod")
    val cod: String?,
    @SerializedName("list")
    val list: List<Item0?>?,
    @SerializedName("message")
    val message: Int?
) {
    data class City(
        @SerializedName("coord")
        val coord: Coord?,
        @SerializedName("country")
        val country: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("population")
        val population: Int?,
        @SerializedName("sunrise")
        val sunrise: Int?,
        @SerializedName("sunset")
        val sunset: Int?,
        @SerializedName("timezone")
        val timezone: Int?
    ) {
        data class Coord(
            @SerializedName("lat")
            val lat: Double?,
            @SerializedName("lon")
            val lon: Double?
        )
    }

    data class Item0(
        @SerializedName("clouds")
        val clouds: Clouds?,
        @SerializedName("dt")
        val dt: Long?,
        @SerializedName("dt_txt")
        val dtTxt: String?,
        @SerializedName("main")
        val main: Main?,
        @SerializedName("pop")
        val pop: Double?,
        @SerializedName("rain")
        val rain: Rain?,
        @SerializedName("sys")
        val sys: Sys?,
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
            @SerializedName("temp_kf")
            val tempKf: Double?,
            @SerializedName("temp_max")
            val tempMax: Double?,
            @SerializedName("temp_min")
            val tempMin: Double?
        )

        data class Rain(
            @SerializedName("3h")
            val h: Double?
        )

        data class Sys(
            @SerializedName("pod")
            val pod: String?
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
}