package com.example.chaseweather.repositories

import com.example.chaseweather.db.WeatherDatabase
import com.example.chaseweather.model.WeatherDataResponse
import com.example.chaseweather.model.WeatherDetail
import com.example.chaseweather.network.ApiHelper
import com.example.chaseweather.network.SafeApiRequest
import javax.inject.Inject
import javax.inject.Singleton

interface WeatherRepository {
    suspend fun findCityWeather(cityName: String): WeatherDataResponse
    suspend fun addWeather(weatherDetail: WeatherDetail)
    suspend fun fetchWeatherDetail(cityName: String): WeatherDetail?
    suspend fun fetchAllWeatherDetails(): List<WeatherDetail>
}

@Singleton
class WeatherRepositoryImp @Inject constructor(
    private val api: ApiHelper,
    private val db: WeatherDatabase
) : WeatherRepository, SafeApiRequest() {

    override suspend fun findCityWeather(cityName: String): WeatherDataResponse = apiRequest {
        api.findCityWeatherData(cityName)
    }

    override suspend fun addWeather(weatherDetail: WeatherDetail) {
        db.getWeatherDao().addWeather(weatherDetail)
    }

    override suspend fun fetchWeatherDetail(cityName: String): WeatherDetail? =
        db.getWeatherDao().fetchWeatherByCity(cityName)

    override suspend fun fetchAllWeatherDetails(): List<WeatherDetail> =
        db.getWeatherDao().fetchAllWeatherDetails()
}
