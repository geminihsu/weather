package com.example.chaseweather.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.chaseweather.db.WeatherDatabase
import com.example.chaseweather.db.WeatherDetailDao
import com.example.chaseweather.util.AppConstants
import com.example.chaseweather.util.AppConstants.LAST_CITY_SHARED_PREFERENCE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn
@Module
class TestModule {
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
            AppConstants.DATA_BASE_NAME
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSharedPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(LAST_CITY_SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }
}