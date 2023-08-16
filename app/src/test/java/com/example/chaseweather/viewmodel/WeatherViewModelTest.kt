package com.example.chaseweather.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.chaseweather.TestCoroutineRule
import com.example.chaseweather.model.WeatherDataResponse
import com.example.chaseweather.model.WeatherDetail
import com.example.chaseweather.repositories.WeatherRepository
import com.example.chaseweather.util.AppConstants
import com.example.chaseweather.util.AppConstants.LAST_CITY_SHARED_PREFERENCE
import com.example.chaseweather.util.AppUtils
import com.example.chaseweather.util.Event
import com.example.chaseweather.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.reset
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    lateinit var repository: WeatherRepository

    private lateinit var weatherViewModel: WeatherViewModel

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private val dispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var uiStateObserver: Observer<Event<State<WeatherDetail>>>

    val response = WeatherDataResponse(
        base = "",
        clouds = WeatherDataResponse.Clouds(1),
        cod = 1,
        coord = WeatherDataResponse.Coord(-122.3344, 38.4832),
        dt = 1,
        id = 1,
        main = WeatherDataResponse.Main(
            feelsLike = 30.0,
            grndLevel = 2,
            humidity = 2,
            pressure = 2,
            seaLevel = 2,
            temp = 20.0,
            tempMax = 20.0,
            tempMin = 20.0
        ),
        name = "napa",
        sys = WeatherDataResponse.Sys("", 2, 2),
        timezone = 2,
        visibility = 0,
        weather = listOf(WeatherDataResponse.Weather("", "", 1, "")),
        wind = WeatherDataResponse.Wind(2, 2.0, 2.0)

    )

    @Before
    fun setUp() = runTest(dispatcher) {
        Dispatchers.setMain(dispatcher)
        weatherViewModel = WeatherViewModel(sharedPreferences, repository)
        weatherViewModel.getWeatherLiveData().observeForever(uiStateObserver)
        reset(editor)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    @Test
    fun `should store city weather data to database`() = runTest {
        val expectResult = WeatherDetail(
            id = response.id,
            temp = response.main.temp,
            icon = response.weather.first().icon,
            cityName = response.name,
            countryName = response.sys.country,
            dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
        )
        given(repository.findCityWeather("napa")).willReturn(response)
        weatherViewModel.findCityWeather("napa")
        verify(repository).addWeather(expectResult)
    }

    @Test
    fun `fetch city weather data when the timestamp expired`() = runTest {
            val weatherDetail = WeatherDetail(
                id = response.id,
                temp = response.main.temp,
                icon = response.weather.first().icon,
                cityName = response.name,
                countryName = response.sys.country,
                dateTime = "Mon, 26 June 2021 09:22:12"
            )

            weatherViewModel.fetchWeatherDetailInfo("napa")
            assertEquals(AppUtils.isTimeExpired(weatherDetail.dateTime), true)
    }

    @Test
    fun `return city weather data when the timestamp valid`() = runTest {
        given(sharedPreferences.edit()).willReturn(editor)
        testCoroutineRule.runBlockingTest {
            Mockito.doReturn(emptyList<WeatherDetail>())
                .`when`(repository)
                .fetchWeatherDetail("napa")

            val weatherDetail = WeatherDetail(
                id = response.id,
                temp = response.main.temp,
                icon = response.weather.first().icon,
                cityName = response.name,
                countryName = response.sys.country,
                dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
            )
            val expectWeatherDetail = Event(
                State.success(
                    weatherDetail
                )
            )
            given(repository.fetchWeatherDetail("napa")).willReturn(weatherDetail)
            weatherViewModel.fetchWeatherDetailInfo("napa")
            verify(uiStateObserver).onChanged(expectWeatherDetail)
            weatherViewModel.getWeatherLiveData().removeObserver(uiStateObserver)
            reset(editor)
        }
    }

    @Test
    fun `fetch all weather data from database`() = runTest {
        testCoroutineRule.runBlockingTest {
            val weatherDetail = WeatherDetail(
                id = response.id,
                temp = response.main.temp,
                icon = response.weather.first().icon,
                cityName = response.name,
                countryName = response.sys.country,
                dateTime = AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT_1)
            )
            val expectWeatherDetail = Event(
                State.success(
                    weatherDetail
                )
            )

            given(repository.findCityWeather("napa")).willReturn(response)
            weatherViewModel.findCityWeather("napa")
            weatherViewModel.fetchAllWeatherDetailsFromDb()
            verify(uiStateObserver).onChanged(expectWeatherDetail)
            weatherViewModel.getWeatherLiveData().removeObserver(uiStateObserver)
            reset(editor)
        }
    }

    @Test
    fun `return Error state`() = runTest {
        testCoroutineRule.runBlockingTest {
            val errorMessage = "Error Message For You"
            Mockito.doThrow(RuntimeException(errorMessage))
                .`when`(repository)
                .findCityWeather("napa")

            weatherViewModel.findCityWeather("napa")
            verify(uiStateObserver).onChanged(Event(State.error(errorMessage)))
            weatherViewModel.getWeatherLiveData().removeObserver(uiStateObserver)
        }
    }

    @Test
    fun `retrieve last City if sharePreference not empty`() = runTest {
        reset(editor)
        given(sharedPreferences.edit()).willReturn(editor)
        given(sharedPreferences.getString(LAST_CITY_SHARED_PREFERENCE, "")).willReturn("napa")
        testCoroutineRule.runBlockingTest {
            weatherViewModel.searchLastCityValue()
            verify(sharedPreferences).getString(LAST_CITY_SHARED_PREFERENCE, "")
            reset(editor)
        }
    }

    @Test
    fun `store last City after search result`() = runTest {
        given(sharedPreferences.edit()).willReturn(editor)
        testCoroutineRule.runBlockingTest {
            weatherViewModel.fetchWeatherDetailInfo("napa")
            verify(editor).putString(LAST_CITY_SHARED_PREFERENCE, "napa")
            reset(editor)
        }
    }
}