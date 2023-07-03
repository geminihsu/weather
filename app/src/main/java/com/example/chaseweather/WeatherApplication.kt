package com.example.chaseweather

import com.example.chaseweather.di.DiApplication
import com.example.chaseweather.di.WeatherComponent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : DiApplication() {

    override fun onCreate() {
        super.onCreate()

        prepareApplicationComponent()
    }

    private fun prepareApplicationComponent() {
        DiApplication.appComponent = WeatherComponent.build(this)
    }

    companion object {
        @JvmStatic
        val appComponent: WeatherComponent
            get() = DiApplication.appComponent
    }
}