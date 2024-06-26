package ie.setu.familytrip.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable