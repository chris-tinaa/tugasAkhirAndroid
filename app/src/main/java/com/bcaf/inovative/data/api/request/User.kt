package com.bcaf.inovative.data.api.request

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class User(

        @SerializedName("email") val email: String,

        @SerializedName("userName") val userName: String,
        @SerializedName("password") val password: String,
        @SerializedName("nama") val nama: String,
        @SerializedName("jenisKelamin") val jenisKelamin: String

    ) : Serializable
