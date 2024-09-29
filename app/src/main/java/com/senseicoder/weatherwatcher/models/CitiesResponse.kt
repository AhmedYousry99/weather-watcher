package com.senseicoder.weatherwatcher.models


import com.google.gson.annotations.SerializedName

/**
[
  {
    "name": "San Francisco",
    "latitude": 37.7562,
    "longitude": -122.443,
    "country": "US",
    "population": 3592294,
    "is_capital": false
  }
]
*/
class CitiesResponse : ArrayList<CitiesResponse.CityResponseItem>(){
    data class CityResponseItem(
        @SerializedName("country")
        val country: String?,
        @SerializedName("is_capital")
        val isCapital: Boolean?,
        @SerializedName("latitude")
        val latitude: Double?,
        @SerializedName("longitude")
        val longitude: Double?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("population")
        val population: Int?
    ){
        override fun toString(): String {
            return (name ?: "null") + ", " + (country ?: "null")
        }
    }
}