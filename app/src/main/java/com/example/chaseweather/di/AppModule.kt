package com.example.chaseweather.di

import android.content.Context
import android.content.SharedPreferences
import com.example.chaseweather.network.ApiHelper
import com.example.chaseweather.network.ApiHelperImpl
import com.example.chaseweather.network.ApiInterface
import com.example.chaseweather.repositories.WeatherRepository
import com.example.chaseweather.repositories.WeatherRepositoryImp
import com.example.chaseweather.util.AppConstants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun provideWeatherRepository(weatherImp: WeatherRepositoryImp): WeatherRepository

    @Binds
    abstract fun provideWeatheyrApiHelper(piHelper: ApiHelperImpl): ApiHelper

    companion object {
        @Provides
        fun provideBaseUrl() = AppConstants.BASE_URL

        @Singleton
        @Provides
        fun provideOtherInterceptorOkHttpClient(
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        fun provideRetrofit(
            okHttpClient: OkHttpClient,
            BASE_URL: String
        ): Retrofit =
            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build()

        @Provides
        @Singleton
        fun provideApiInterface(retrofit: Retrofit) = retrofit.create(ApiInterface::class.java)

        @Provides
        fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
            return context.getSharedPreferences(AppConstants.LAST_CITY_SHARED_PREFERENCE, Context.MODE_PRIVATE)
        }

        @Provides
        fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }
}