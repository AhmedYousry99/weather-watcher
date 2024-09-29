package com.senseicoder.weatherwatcher.models

data class ReverseGeocodeDTO (var arabic: String, var english: String, var country: String, var latitude: Double, var longitude: Double, var state: String){

    companion object{
        fun fromReverseGeocodeResponse(response: ReverseGeocodeResponse): ReverseGeocodeDTO{
            return if(response.isEmpty()){
                ReverseGeocodeDTO(
                    arabic = "",
                    english = "",
                    country = "",
                    longitude = 0.0,
                    latitude = 0.0,
                    state = ""
                )
            }else{
                response[0].run{
                    ReverseGeocodeDTO(
                        arabic = localNames?.ar ?: name?: "",
                        english = localNames?.en ?: name ?: "",
                        country = country ?: "",
                        longitude = lon ?: 0.0,
                        latitude = lat ?: 0.0,
                        state = state ?: ""
                    )
                }
            }

        }
    }
}