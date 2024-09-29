package com.senseicoder.weatherwatcher.models


import com.google.gson.annotations.SerializedName

/**
{
  "result": {
    "uv": 0,
    "uv_time": "2024-09-21T19:30:13.338Z",
    "uv_max": 3.3631,
    "uv_max_time": "2024-09-21T11:54:46.687Z",
    "ozone": 332.5,
    "ozone_time": "2023-04-12T15:04:31.773Z",
    "safe_exposure_time": {
      "st1": null,
      "st2": null,
      "st3": null,
      "st4": null,
      "st5": null,
      "st6": null
    },
    "sun_info": {
      "sun_times": {
        "solarNoon": "2024-09-21T11:54:46.687Z",
        "nadir": "2024-09-20T23:54:46.687Z",
        "sunrise": "2024-09-21T05:46:34.684Z",
        "sunset": "2024-09-21T18:02:58.690Z",
        "sunriseEnd": "2024-09-21T05:50:00.257Z",
        "sunsetStart": "2024-09-21T17:59:33.117Z",
        "dawn": "2024-09-21T05:13:13.215Z",
        "dusk": "2024-09-21T18:36:20.159Z",
        "nauticalDawn": "2024-09-21T04:33:42.246Z",
        "nauticalDusk": "2024-09-21T19:15:51.127Z",
        "nightEnd": "2024-09-21T03:52:25.868Z",
        "night": "2024-09-21T19:57:07.505Z",
        "goldenHourEnd": "2024-09-21T06:30:33.742Z",
        "goldenHour": "2024-09-21T17:18:59.632Z"
      },
      "sun_position": {
        "azimuth": 1.91253745219897,
        "altitude": -0.2509308669805904
      }
    }
  }
}
*/
data class UVResponse(
    @SerializedName("result")
    val result: Result?
) {
    data class Result(
        @SerializedName("uv")
        val uv: Double?,
        @SerializedName("uv_max")
        val uvMax: Double?,

    )
}