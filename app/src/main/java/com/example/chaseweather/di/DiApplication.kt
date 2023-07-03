package com.example.chaseweather.di

import android.app.Application

abstract class DiApplication: Application() {

    companion object {
        lateinit var appComponent: WeatherComponent
    }
}