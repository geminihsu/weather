package com.example.chaseweather.network

import com.example.chaseweather.model.WeatherDataResponse
import com.example.chaseweather.util.AppConstants
import com.example.chaseweather.util.AppConstants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    // </editor-fold>

   /* companion object {
        operator fun invoke(
        ): ApiInterface {

            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }
    }*/
}
