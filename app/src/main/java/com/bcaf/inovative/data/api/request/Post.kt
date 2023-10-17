package com.bcaf.inovative.data.api.request

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("idUser")
    val userId: Int,

    @SerializedName("name")
    val name: String
)

data class Post2(
    @SerializedName("judulPost")
    val title: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("deskripsi")
    val description: String,

    @SerializedName("upvote")
    val upvote: Int,

    @SerializedName("fotoKonten")
    val fotoKonten: String?,

    @SerializedName("user")
    val user: Post
)