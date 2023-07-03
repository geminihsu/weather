package com.example.chaseweather.di

import android.content.Context
import com.example.chaseweather.repositories.WeatherRepository
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@EntryPoint
interface WeatherComponent {

    companion object {
        fun build(@ApplicationContext context: Context): WeatherComponent {

            val appComponent = EntryPoints.get(context, WeatherComponent::class.java)
            return appComponent
        }
    }
}