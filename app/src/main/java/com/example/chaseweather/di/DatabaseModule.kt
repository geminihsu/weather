package com.example.chaseweather.di

import android.content.Context
import androidx.room.Room
import com.example.chaseweather.database.WeatherDatabase
import com.example.chaseweather.database.WeatherDetailDao
import com.example.chaseweather.util.AppConstants.DATA_BASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabaseDao(weatherDatabase: WeatherDatabase): WeatherDetailDao {
        return weatherDatabase.getWeatherDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): WeatherDatabase {

        return Room.databaseBuilder(
            appContext,
            WeatherDatabase::class.java,
            DATA_BASE_NAME
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
}