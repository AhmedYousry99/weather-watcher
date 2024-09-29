package com.senseicoder.weatherwatcher.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.senseicoder.weatherwatcher.models.AlertDTO
import com.senseicoder.weatherwatcher.models.FavoriteDTO
import com.senseicoder.weatherwatcher.models.ForecastDTO
import com.senseicoder.weatherwatcher.models.WeatherDTO
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {
    @Query("SELECT * From ${WeatherDTO.ENTITY_NAME} WHERE location = :location")
    fun getCurrentWeatherData(location: String): Flow<List<WeatherDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrentWeatherData(weatherDTO: WeatherDTO) : Long

    @Query("SELECT * From ${AlertDTO.ENTITY_NAME}")
    fun getAlerts(): Flow<List<AlertDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlert(alertDTO: AlertDTO) : Long

    @Query("DELETE From ${AlertDTO.ENTITY_NAME} WHERE id = :alertId")
    fun deleteAlert(alertId: Long) : Int

    @Query("SELECT * From ${FavoriteDTO.ENTITY_NAME}")
    fun getFavorites(): Flow<List<FavoriteDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(favoriteDTO: FavoriteDTO) : Long

    @Query("DELETE From ${FavoriteDTO.ENTITY_NAME} WHERE location = :location")
    fun deleteFavorite(location: String) : Int

    /*@Delete
    fun delete(productDTO: ProductDTO) : Int*/
}

interface LocalDataSource{
    suspend fun getCurrentWeatherData(location: String): Flow<List<WeatherDTO>>
    suspend fun insertCurrentWeatherData(weatherDTO: WeatherDTO): Long
    suspend fun getAlerts(): Flow<List<AlertDTO>>
    suspend fun insertAlert(alertDTO: AlertDTO) : Long
    suspend fun deleteAlert(alertId: Long) : Int
    fun getFavorites(): Flow<List<FavoriteDTO>>
    fun insertFavorite(favoriteDTO: FavoriteDTO) : Long
    fun deleteFavorite(location: String) : Int
    /*suspend fun delete(weatherDTO: WeatherDTO): Int*/
}

class LocalDataSourceImpl(private val weatherDAO: WeatherDAO) : LocalDataSource{
    override suspend fun getCurrentWeatherData(location: String): Flow<List<WeatherDTO>> {
        return weatherDAO.getCurrentWeatherData(location)
    }

    override suspend fun insertCurrentWeatherData(weatherDTO: WeatherDTO): Long {
        return weatherDAO.insertCurrentWeatherData(weatherDTO)
    }

    override suspend fun getAlerts(): Flow<List<AlertDTO>> {
        return weatherDAO.getAlerts()
    }

    override suspend fun insertAlert(alertDTO: AlertDTO): Long {
        return weatherDAO.insertAlert(alertDTO)
    }

    override suspend fun deleteAlert(alertId: Long): Int {
        return weatherDAO.deleteAlert(alertId)
    }

    override fun getFavorites(): Flow<List<FavoriteDTO>> {
        return weatherDAO.getFavorites()
    }

    override fun insertFavorite(favoriteDTO: FavoriteDTO): Long {
        return weatherDAO.insertFavorite(favoriteDTO)
    }

    override fun deleteFavorite(location: String): Int {
        return weatherDAO.deleteFavorite(location)
    }

}

@Database(entities = [WeatherDTO::class, ForecastDTO::class, AlertDTO::class, FavoriteDTO::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract val weatherDAO: WeatherDAO

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return instance ?:  synchronized(this){
                val instance = databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java, "weather_watcher_db"
                )
                    .build()
                this.instance = instance
                instance
            }
        }
    }
}

