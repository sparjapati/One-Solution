package edu.vermaSanjay15907.oneSolution.models

data class User(
    var isOfficer :Boolean = false,
    var fname: String = "",
    var lname: String = "",
    var mobileNumber: String = "",
    var gender: String = "",
    var address: Address = Address()
)
