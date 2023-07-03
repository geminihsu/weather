package com.example.chaseweather.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chaseweather.repositories.WeatherRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeatherViewModelFactory @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val repository: WeatherRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(sharedPreferences, repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}