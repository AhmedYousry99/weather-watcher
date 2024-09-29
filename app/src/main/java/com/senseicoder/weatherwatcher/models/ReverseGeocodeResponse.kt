package com.senseicoder.weatherwatcher.models


import com.google.gson.annotations.SerializedName

/**
[
  {
    "name": "Alexandria",
    "local_names": {
      "es": "Alejandría",
      "el": "Αλεξάνδρεια",
      "ko": "알렉산드리아",
      "fa": "اسکندریه",
      "sk": "Alexandria",
      "ku": "Îskenderiye",
      "ca": "Alexandria",
      "ks": "اسکندریہ",
      "mr": "सिकन्दरिया",
      "zh": "亚历山大港",
      "mi": "Arekehānaria",
      "ur": "اسکندریہ",
      "cs": "Alexandrie",
      "hr": "Aleksandrija",
      "la": "Alexandria",
      "kn": "ಸಿಕಂದರಿಯಾ",
      "he": "אלכסנדריה",
      "hu": "Alexandria",
      "gr": "Ἀλεξάνδρεια",
      "nl": "Alexandrië",
      "en": "Alexandria",
      "ru": "Александрия",
      "eo": "Aleksandrio",
      "lt": "Aleksandrija",
      "fr": "Alexandrie",
      "it": "Alessandria",
      "ml": "അലക്സാണ്ട്രിയ",
      "et": "Aleksandria",
      "ps": "اسکندریه",
      "de": "Alexandria",
      "pt": "Alexandria",
      "pl": "Aleksandria",
      "ar": "الإسكندرية",
      "hi": "सिकन्दरिया",
      "ms": "Iskandariah",
      "tr": "İskenderiye",
      "no": "Alexandria",
      "sv": "Alexandria",
      "sd": "اسڪندريہ",
      "fi": "Aleksandria",
      "oc": "Alexandria"
    },
    "lat": 31.199004,
    "lon": 29.894378,
    "country": "EG",
    "state": "Alexandria"
  }
]
*/
class ReverseGeocodeResponse : ArrayList<ReverseGeocodeResponse.ReverseGeocodeResponseItem>(){
    data class ReverseGeocodeResponseItem(
        @SerializedName("country")
        val country: String?,
        @SerializedName("lat")
        val lat: Double?,
        @SerializedName("local_names")
        val localNames: LocalNames?,
        @SerializedName("lon")
        val lon: Double?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("state")
        val state: String?
    ) {
        data class LocalNames(
            @SerializedName("ar")
            val ar: String?,
            @SerializedName("en")
            val en: String?,
        )
    }
}