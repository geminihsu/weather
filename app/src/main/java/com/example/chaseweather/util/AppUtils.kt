package com.example.chaseweather.util

import android.annotation.SuppressLint
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.chaseweather.R
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.collections.HashMap

object AppUtils {
    val iconAssetsMap : HashMap<String, Int> = hashMapOf(
        "01d" to R.drawable.d1,
        "02d" to R.drawable.d2,
        "03d" to R.drawable.d3,
        "04d" to R.drawable.d4,
        "09d" to R.drawable.d9,
        "11d" to R.drawable.d11,
        "13d" to R.drawable.d13,
        "50d" to R.drawable.d50,
    )
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDateTime(dateFormat: String): String =
        SimpleDateFormat(dateFormat).format(Date())

    @SuppressLint("SimpleDateFormat")
    fun isTimeExpired(dateTimeSavedWeather: String?): Boolean {
        dateTimeSavedWeather?.let {
            val currentDateTime = Date()
            val savedWeatherDateTime =
                SimpleDateFormat(AppConstants.DATE_FORMAT_1).parse(it)
            val diff: Long = currentDateTime.time - savedWeatherDateTime.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            if (minutes > 10)
                return true
        }
        return false
    }

    fun setGlideImage(image: ImageView, url: String) {
        Glide.with(image).load(url)
            .thumbnail(0.5f)
            .into(image)
    }
}