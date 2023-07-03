package com.example.chaseweather.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaseweather.model.WeatherDataResponse
import com.example.chaseweather.model.WeatherDetail
import com.example.chaseweather.repositories.WeatherRepository
import com.example.chaseweather.util.ApiException
import com.example.chaseweather.util.AppConstants
import com.example.chaseweather.util.AppConstants.LAST_CITY_SHARED_PREFERENCE
import com.example.chaseweather.util.AppUtils
import com.example.chaseweather.util.Event
import com.example.chaseweather.util.NoInternetException
import com.example.chaseweather.util.State
import com.google.android.gms.common.util.VisibleForTesting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val repository: WeatherRepository
    ) : ViewModel() {

    private val _weatherLiveData =
        MutableLiveData<Event<State<WeatherDetail>>>()

    val weatherLiveData: LiveData<Event<State<WeatherDetail>>>
        get() = _weatherLiveData

    private val _weatherDetailListLiveData =
        MutableLiveData<Event<State<List<WeatherDetail>>>>()
    val weatherDetailListLiveData: LiveData<Event<State<List<WeatherDetail>>>>
        get() = _weatherDetailListLiveData

    private lateinit var weatherResponse: WeatherDataResponse

    fun findCityWeather(cityName: String) {
        _weatherLiveData.postValue(Event(State.loading()))
        viewModelScope.launch(Dispatchers.Main) {
            try {
                weatherResponse = repository.findCityWeather(cityName)
                withContext(Dispatchers.Main) {
                    val weatherDetail = addWeatherDetailIntoDb(weatherResponse)
                    _weatherLiveData.postValue(
                        Event(
                            State.success(
                                weatherDetail
                            )
                        )
                    )
                }
            } catch (e: ApiException) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(Event(State.error(e.message ?: "")))
                }
            } catch (e: NoInternetException) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(Event(State.error(e.message ?: "")))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _weatherLiveData.postValue(
                        Event(
                            State.error(
                                e.message ?: ""
                            )
                        )
                    )
                }
            }
        }
    }

    private fun getLastSearchCity(): String? {
        return sharedPreferences.getString(LAST_CITY_SHARED_PREFERENCE, "")
    }

    private fun storeLastCitySearch(cityName: String) {
        with (sharedPreferences.edit()) {
            putString(LAST_CITY_SHARED_PREFERENCE, cityName)
            apply()
        }
    }

    fun searchLastCityValue(): Boolean {
        val lastCity = getLastSearchCity() ?: ""
        if (lastCity.isNotEmpty() && !lastCity.isNullOrEmpty()) {
            fetchWeatherDetailInfo(lastCity)
            return true
        }

        return false
    }

    fun fetchWeatherDetailInfo(cityName: String) {
        fetchWeatherDetailFromDb(cityName)
        fetchAllWeatherDetailsFromDb()
    }

    private suspend fun addWeatherDetailIntoDb(weatherResponse: WeatherDataResponse): WeatherDetail {
        val weatherDetail = WeatherDetail()
        weatherDetail.id = weatherResponse.id
        weatherDetail.icon = weatherResponse.weather.first().icon
        weatherDetail.cityName = weatherResponse.name.toLowerCase()
        weatherDetail.countryName = weatherResponse.sys.country
        weatherDetail.temp = weatherResponse.main.temp
        weatherDetail.dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
        repository.addWeather(weatherDetail)
        return weatherDetail
    }

    private fun fetchWeatherDetailFromDb(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storeLastCitySearch(cityName)
            val weatherDetail = repository.fetchWeatherDetail(cityName.toLowerCase())
                if (weatherDetail != null) {
                    // Return true of current date and time is greater then the saved date and time of weather searched
                    if (AppUtils.isTimeExpired(weatherDetail.dateTime)) {
                        findCityWeather(cityName)
                    } else {
                        _weatherLiveData.postValue(
                            Event(
                                State.success(
                                    weatherDetail
                                )
                            )
                        )
                    }

                } else {
                    findCityWeather(cityName)
                }
        }
    }

    @VisibleForTesting
    fun fetchAllWeatherDetailsFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherDetailList = repository.fetchAllWeatherDetails()
            withContext(Dispatchers.Main) {
                _weatherDetailListLiveData.postValue(
                    Event(
                        State.success(weatherDetailList)
                    )
                )
            }
        }
    }

    fun getWeatherLiveData():  MutableLiveData<Event<State<WeatherDetail>>> {
        return _weatherLiveData
    }
}
