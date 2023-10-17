package com.bcaf.inovative.data.api.request


import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("userName")
    var username: String,
    @SerializedName("password")
    var password: String
)


