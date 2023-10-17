package com.bcaf.inovative.data.api.request

import com.google.gson.annotations.SerializedName


    data class Reply(

        @SerializedName("user") val user: User3,
        @SerializedName("comment") val comment: String,
        @SerializedName("post") val post: DataItem,



    )
