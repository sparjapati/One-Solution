package edu.vermaSanjay15907.oneSolution.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    var country:String = "",
    var state:String = "",
    var district:String ="",
    var cityOrVillage:String ="",
    var nearByLocation:String ="")
    :Parcelable
