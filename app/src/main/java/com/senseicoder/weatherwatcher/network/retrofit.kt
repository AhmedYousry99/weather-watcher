package com.senseicoder.weatherwatcher.network

import com.senseicoder.weatherwatcher.BuildConfig
import com.senseicoder.weatherwatcher.models.CitiesResponse
import com.senseicoder.weatherwatcher.models.ForecastResponse
import com.senseicoder.weatherwatcher.models.ReverseGeocodeResponse
import com.senseicoder.weatherwatcher.models.UVResponse
import com.senseicoder.weatherwatcher.models.WeatherResponse
import com.senseicoder.weatherwatcher.utils.enums.Units
import com.senseicoder.weatherwatcher.utils.global.Constants
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File

interface WeatherService {
    @GET(WEATHER)
    suspend fun getWeatherToday(@Query(LONGITUDE) longitude: Double,
                                @Query(LATITUDE) latitude: Double,
                                @Query(UNITS) unit: Units = Units.standard,
                                @Query(LANG) lang: String = Constants.API.Lang.en,
                                @Query(APP_ID) apiKey: String = BuildConfig.API_KEY
                                ) : Response<WeatherResponse>

    @GET(FORECAST)
    suspend fun getWeatherForecast(@Query(LONGITUDE) longitude: Double,
                                @Query(LATITUDE) latitude: Double,
                                @Query(UNITS) unit: Units = Units.standard,
                                @Query(LANG) lang: String = Constants.API.Lang.en,
                                @Query(COUNT) count: Int = 0,
                                @Query(APP_ID) apiKey: String = BuildConfig.API_KEY) : Response<ForecastResponse>

    @GET
    suspend fun getLocationDescription(@Url url:String = Constants.API.REVERSE_GEOCODE_BASE_URL + REVERSE,
                                       @Query(LONGITUDE) longitude: Double,
                                       @Query(LATITUDE) latitude: Double,
                                       @Query(LIMIT) limit: Int = 1,
                                       @Query(APP_ID) apiKey: String = BuildConfig.API_KEY) : Response<ReverseGeocodeResponse>

    @GET
    @Headers("X-Api-Key: " + BuildConfig.CITIES_API_KEY)
    suspend fun getCities(@Url url:String = Constants.API.CITIES_BASE_URL + CITY,
                                       @Query(NAME) name: String,
                                       @Query(LIMIT) limit: Int = 30) : Response<CitiesResponse>

    @GET
    @Headers("x-access-token: " + BuildConfig.UV_API_KEY)
    suspend fun getUVToday(@Url url: String = Constants.API.UV_BASE_URL+UV,
        @Query("lng") longitude: Double,
                                   @Query("lat") latitude: Double,
    ) : Response<UVResponse>

    companion object ProductsServiceResponseKeys {
        /*currentDate : none(from phone)
        currentTime : none(from phone)
        units (standard (which is kelvin), metric or imperial)
        lang (either ar or en in request itself)*/
        const val APP_ID: String = "appid"
        const val WEATHER: String = "weather"
        const val FORECAST: String = "forecast"
        const val REVERSE: String = "reverse"
        const val CITY: String = "city"
        const val NAME: String = "name"
        const val DIRECT: String = "direct"
        const val Q: String = "q"
        const val UNITS: String = "units"
        const val LANG: String = "lang"
        const val LATITUDE: String = "lat"
        const val LONGITUDE: String = "lon"
        const val COUNT: String = "cnt"
        const val UV: String = "uv"
        const val LIMIT: String = "limit"
    }
}

class RemoteDataSourceImpl private constructor(cacheDirectory: File) : RemoteDataSource{

    private val weatherService: WeatherService

    init {
        val cacheSize = 10 * 1024 * 1024
        val cache = Cache(cacheDirectory, cacheSize.toLong())

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.API.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        weatherService = retrofit.create(WeatherService::class.java)
    }

    override suspend fun getWeatherToday(longitude: Double, latitude: Double) : Response<WeatherResponse> {
        return weatherService.getWeatherToday(longitude = longitude, latitude = latitude)
    }

    override suspend fun getWeatherForecast(
        longitude: Double,
        latitude: Double
    ): Response<ForecastResponse> {
        return weatherService.getWeatherForecast(longitude = longitude, latitude = latitude)
    }

    override suspend fun getUVToday(longitude: Double, latitude: Double): Response<UVResponse> {
        return weatherService.getUVToday(longitude = longitude, latitude = latitude)
    }

    override suspend fun getReverseGeocode(
        longitude: Double,
        latitude: Double
    ): Response<ReverseGeocodeResponse> {
        return weatherService.getLocationDescription(longitude = longitude, latitude = latitude)
    }

    override suspend fun getCities(query: String): Response<CitiesResponse> {
        return weatherService.getCities(name = query)
    }

    companion object {
        const val TAG: String = "RemoteDataSourceImpl"
        @Volatile
        private var instance: RemoteDataSourceImpl? = null
        fun getInstance(cacheDirectory: File? = null): RemoteDataSourceImpl {
            return instance ?: synchronized(this){
                val instance =
                    RemoteDataSourceImpl(cacheDirectory!!)
                this.instance = instance
                instance
            }
        }
    }

}

/*object RetrofitHelper{
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://dummyjson.com/")
        .build()
}

object API{
    val retrofitService: ProductsService by lazy {
        RetrofitHelper.retrofit.create(ProductsService::class.java)
    }
}*/

interface RemoteDataSource {
    suspend fun getWeatherToday(longitude: Double, latitude: Double) : Response<WeatherResponse>
    suspend fun getWeatherForecast(longitude: Double, latitude: Double) : Response<ForecastResponse>
    suspend fun getUVToday(longitude: Double, latitude: Double) : Response<UVResponse>
    suspend fun getReverseGeocode(longitude: Double, latitude: Double) : Response<ReverseGeocodeResponse>
    suspend fun getCities(query: String) : Response<CitiesResponse>
}
