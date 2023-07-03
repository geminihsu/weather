package com.example.chaseweather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chaseweather.model.WeatherDetail

@Database(entities = [WeatherDetail::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDetailDao
}
