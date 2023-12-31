package com.example.chaseweather.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chaseweather.model.WeatherDetail

@Dao
interface WeatherDetailDao {

    /**
     * Duplicate values are replaced in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWeather(weatherDetail: WeatherDetail)

    @Query("SELECT * FROM ${WeatherDetail.TABLE_NAME} WHERE cityName = :cityName")
    suspend fun fetchWeatherByCity(cityName: String): WeatherDetail?

    @Query("SELECT * FROM ${WeatherDetail.TABLE_NAME}")
    suspend fun fetchAllWeatherDetails(): List<WeatherDetail>

}
