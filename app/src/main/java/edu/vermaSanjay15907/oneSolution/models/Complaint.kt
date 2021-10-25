package edu.vermaSanjay15907.oneSolution.models

import android.os.Parcelable
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS_PENDING
import kotlinx.parcelize.Parcelize

@Parcelize
data class Complaint(
    var complainedBy: String = "",
    var address: Address = Address(),
    var date: String = "",
//    var images: ArrayList<String> = ArrayList(),
    var images: String = "",
    var status: String = STATUS_PENDING,
    var description: String = "",
    var complaintId: String = ""
):Parcelable