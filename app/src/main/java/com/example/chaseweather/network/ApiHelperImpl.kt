package com.example.chaseweather.network

import com.example.chaseweather.model.WeatherDataResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiInterface: ApiInterface) : ApiHelper {
    override suspend fun findCityWeatherData(
        q: String,
        units: String,
        appid: String
    ): Response<WeatherDataResponse> = apiInterface.findCityWeatherData(q, units, appid)
}