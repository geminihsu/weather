package com.example.chaseweather.network

import com.example.chaseweather.model.WeatherDataResponse
import com.example.chaseweather.util.AppConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    // <editor-fold desc="Get Requests">

    @GET("weather")
    suspend fun findCityWeatherData(
        @Query("q") q: String,
        @Query("units") units: String = AppConstants.WEATHER_UNIT,
        @Query("appid") appid: String = AppConstants.API_KEY
    ): Response<WeatherDataResponse>
}
