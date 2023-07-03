package com.example.chaseweather.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chaseweather.R
import com.example.chaseweather.databinding.ListItemSearchedCityTemperatureBinding
import com.example.chaseweather.listener.WeatherListener
import com.example.chaseweather.model.WeatherDetail
import com.example.chaseweather.util.AppConstants
import com.example.chaseweather.util.AppUtils
import com.example.chaseweather.util.getIconDrawable

class SearchedCityAdapter constructor(
    private val context: Context,
   private val listener: WeatherListener
) :
    RecyclerView.Adapter<SearchedCityAdapter.ViewHolder>() {

    private val weatherDetailList = ArrayList<WeatherDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemSearchedCityTemperatureBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_searched_city_temperature,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(weatherDetailList[position])
    }

    override fun getItemCount(): Int = weatherDetailList.size

    fun setData(
        newWeatherDetail: List<WeatherDetail>
    ) {
        weatherDetailList.clear()
        weatherDetailList.addAll(newWeatherDetail)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ListItemSearchedCityTemperatureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(weatherDetail: WeatherDetail) {
            binding.apply {
                val iconCode = weatherDetail.icon?.replace("n", "d")

                if (iconCode != null) {
                    val iconDrawable = getIconDrawable(iconCode)
                    if (iconDrawable != null) {
                        imageWeatherSymbol.setImageDrawable(context.getDrawable(iconDrawable))
                    } else {
                        AppUtils.setGlideImage(
                            imageWeatherSymbol,
                            AppConstants.WEATHER_API_IMAGE_ENDPOINT + "${iconCode}@4x.png")
                    }
                }

                textCityName.text =
                    "${weatherDetail.cityName?.capitalize()}, ${weatherDetail.countryName}"
                textTemperature.text = weatherDetail.temp.toString()
                textDateTime.text = weatherDetail.dateTime
                when {
                    snowDegreeList.contains(iconCode) || warmDegreeList.contains(iconCode) -> weatherLayout.setBackgroundColor(context.getColor(R.color.color_lighteeblblue))
                    rainDegreeList.contains(iconCode) -> weatherLayout.setBackgroundColor(context.getColor(R.color.color_light_blue))
                }
                weatherLayout.setOnClickListener {
                    listener.passCityName(weatherDetail.cityName.toString())
                }
            }
        }
    }
}
