package com.example.chaseweather.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chaseweather.databinding.ListItemSearchedCityTemperatureBinding
import com.example.chaseweather.util.AppUtils.iconAssetsMap


// used for show a toast message in the UI Thread
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Activity.color(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun ListItemSearchedCityTemperatureBinding.getIconDrawable(icon: String): Int? {
   return iconAssetsMap[icon]
}
