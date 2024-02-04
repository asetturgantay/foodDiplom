package com.example.foodapp

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale

class GeocodingHelper(private val context: Context) {
    fun getLatLngFromAddress(locationName: String): LatLng? {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses?.get(0)
                    if (address != null) {
                        return LatLng(address.latitude, address.longitude)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}

data class LatLng(val latitude: Double, val longitude: Double)