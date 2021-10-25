package edu.vermaSanjay15907.oneSolution.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import edu.vermaSanjay15907.oneSolution.R
import edu.vermaSanjay15907.oneSolution.models.Address
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS_PENDING
import edu.vermaSanjay15907.oneSolution.utils.Konstants.STATUS_SOLVED

@BindingAdapter("complaintStatus")
fun ImageView.setComplaintStatus(status: String) {
    if (status == STATUS_PENDING)
        setImageDrawable(resources.getDrawable(R.drawable.yellow))
    else if (status == STATUS_SOLVED)
        setImageDrawable(resources.getDrawable(R.drawable.green))
}

@BindingAdapter("address")
fun TextView.setAddress(address: Address) {
    address.apply {
        text = "$nearByLocation, $cityOrVillage, $district, $state, $country"
    }
}