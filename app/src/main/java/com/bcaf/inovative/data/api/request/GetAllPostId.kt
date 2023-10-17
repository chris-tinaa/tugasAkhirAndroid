package com.bcaf.inovative.data.api.request

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GetAllPostId(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("timestamp")
	val timestamp: String? = null,

	@SerializedName("user")
	val user: UserR,

	@SerializedName("listReply")
	val listReply: List<ListReplyItem>

) : Parcelable

@Parcelize
data class ListReplyItem(

	@field:SerializedName("idReply")
	val idReply: Int? = null,

	@field:SerializedName("comment")
	val comment: String? = null,


	@SerializedName("user")
	val user: UserR,


	@field:SerializedName("tanggalReply")
	val tanggalReply: String? = null
) : Parcelable

@Parcelize
data class UserR(

	@field:SerializedName("idUser")
	val idUser: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("jenisKelamin")
	val jenisKelamin: String? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("listReply")
	val listReply: List<ListReplyItem?>? = null,

	@field:SerializedName("fotoKonten")
	val fotoKonten: String? = null,

	@field:SerializedName("tanggalPost")
	val tanggalPost: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("judulPost")
	val judulPost: String? = null,

	@field:SerializedName("upvote")
	val upvote: Int? = null,

	@SerializedName("user")
	val user: UserR,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("idPost")
	val idPost: Int? = null
) : Parcelable
