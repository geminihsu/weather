package com.example.chaseweather.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaseweather.R
import com.example.chaseweather.databinding.WeatherActivityBinding
import com.example.chaseweather.listener.WeatherListener
import com.example.chaseweather.repositories.WeatherRepository
import com.example.chaseweather.util.AppConstants
import com.example.chaseweather.util.AppUtils
import com.example.chaseweather.util.EventObserver
import com.example.chaseweather.util.State
import com.example.chaseweather.util.hide
import com.example.chaseweather.util.show
import com.example.chaseweather.util.showToast
import com.example.chaseweather.viewmodel.WeatherViewModel
import com.example.chaseweather.viewmodel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

val warmDegreeList = listOf("01d", "02d", "03d")
val rainDegreeList = listOf("04d", "09d", "10d", "11d", "50d")
val snowDegreeList = listOf("13d", "20d")
@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {

    private var permissionId: Int = 2

    @Inject
    lateinit var repository: WeatherRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: WeatherActivityBinding

    private lateinit var searchedCityTemperatureAdapter: SearchedCityAdapter

    @Inject
    lateinit var locationClient: FusedLocationProviderClient

    private lateinit var listener: WeatherListener

    private val viewModel: WeatherViewModel by viewModels {
        WeatherViewModelFactory(sharedPreferences, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WeatherActivityBinding.inflate(layoutInflater)
        listener = WeatherListenerImp()
        setContentView(binding.root)

        searchedLastCity()
        initializeRecyclerView()
        setSearchCity()
        observeAPICall()
    }

    private fun setSearchCity() {
        binding.inputFindCityWeather.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val lastCity = (view as EditText).text.toString()
                viewModel.fetchWeatherDetailInfo(lastCity)
            }
            false
        }
    }

    private fun searchedLastCity() {
        // check if there is last city search result on sharePreference
        // if yes, search last city
        // if not, retrieve the user current city by asking location permission
        if (!viewModel.searchLastCityValue()) {
            getCurrentLocation()
        }
    }

    private fun initializeRecyclerView() {
        searchedCityTemperatureAdapter = SearchedCityAdapter(this, listener)
        val mLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.recyclerViewSearchedCityTemperature.apply {
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = searchedCityTemperatureAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeAPICall() {
        viewModel.weatherLiveData.observe(this, EventObserver { state ->
            when (state) {
                is State.Loading -> {
                    binding.loading.progressOverlay.show()
                }
                is State.Success -> {
                    binding.loading.progressOverlay.hide()
                    binding.imageCloud.hide()
                    binding.textLabelSearchForCity.hide()
                    binding.imageCity.hide()
                    binding.constraintLayoutShowingTemp.show()
                    binding.inputFindCityWeather.text?.clear()
                    state.data.let { weatherDetail ->
                        val iconCode = weatherDetail.icon?.replace("n", "d")
                        AppUtils.setGlideImage(
                            binding.imageWeatherSymbol,
                            AppConstants.WEATHER_API_IMAGE_ENDPOINT + "${iconCode}@4x.png"
                        )
                        syncedWeatherAsset(iconCode)
                        binding.textTodaysDate.text =
                            AppUtils.getCurrentDateTime(AppConstants.DATE_FORMAT)
                        binding.textTemperature.text = weatherDetail.temp.toString()
                        binding.textCityName.text =
                            "${weatherDetail.cityName?.capitalize()}, ${weatherDetail.countryName}"
                    }

                }
                is State.Error -> {
                    binding.loading.progressOverlay.hide()
                    showToast(state.message)
                }
            }
        })

        viewModel.weatherDetailListLiveData.observe(this, EventObserver { state ->
            when (state) {
                is State.Loading -> {
                    binding.loading.progressOverlay.show()
                }
                is State.Success -> {
                    binding.loading.progressOverlay.hide()
                    if (state.data.isEmpty()) {
                        binding.recyclerViewSearchedCityTemperature.hide()
                    } else {
                        binding.recyclerViewSearchedCityTemperature.show()
                        searchedCityTemperatureAdapter.setData(state.data)
                        searchedCityTemperatureAdapter.notifyDataSetChanged()
                    }
                }
                is State.Error -> {
                    binding.loading.progressOverlay.hide()
                    showToast(state.message)
                }
            }
        })
    }

    private fun syncedWeatherAsset(iconCode: String?) {
        when {
             warmDegreeList.contains(iconCode) -> {
                binding.imageWeatherHumanReaction.setImageResource(R.drawable.ic_hot)
                binding.constraintLayoutTodayTemp.setBackgroundColor(resources.getColor(R.color.color_stateblue))
                binding.textLabelToday.setTextColor(resources.getColor(R.color.color_light_blue))
                binding.textTodaysDate.setTextColor(resources.getColor(R.color.color_light_blue))
            }
            rainDegreeList.contains(iconCode) -> {
                binding.imageWeatherHumanReaction.setImageResource(R.drawable.ic_rain)
                binding.constraintLayoutTodayTemp.setBackgroundColor(resources.getColor(R.color.color_light_blue))
                binding.textLabelToday.setTextColor(resources.getColor(R.color.color_stateblue))
                binding.textTodaysDate.setTextColor(resources.getColor(R.color.color_stateblue))
            }
            snowDegreeList.contains(iconCode) -> {
                binding.imageWeatherHumanReaction.setImageResource(R.drawable.ic_snow)
                binding.constraintLayoutTodayTemp.setBackgroundColor(resources.getColor(R.color.color_stateblue))
                binding.textLabelToday.setTextColor(resources.getColor(R.color.color_light_blue))
                binding.textTodaysDate.setTextColor(resources.getColor(R.color.color_light_blue))
            }
        }
    }

    // Check Location Permission start (TODO: move the function to Location Manager)

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        if (list.isNullOrEmpty())
                            return@addOnCompleteListener

                        viewModel.fetchWeatherDetailInfo(list[0].locality)
                    }
                }
            } else {
                showToast(getString(R.string.please_turn_on_location))
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    // Check Location Permission end

    inner class WeatherListenerImp: WeatherListener {
        override fun passCityName(city: String) {
           viewModel.fetchWeatherDetailInfo(city)
        }
    }
}