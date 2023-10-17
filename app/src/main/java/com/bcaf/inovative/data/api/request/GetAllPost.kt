package com.bcaf.inovative.data.api.request



import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class GetAllPost(

	@SerializedName("data")
	val data: List<DataItem?>? = null,



	@SerializedName("success")
	val success: Boolean? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null,

	@SerializedName("timestamp")
	val timestamp: String? = null
)


data class DataItem(

	@SerializedName("listReply")
	val listReply: List<ListReplyItem?>? = null,

	@SerializedName("fotoKonten")
	val fotoKonten: String? = null,

	@field:SerializedName("tanggalPost")
	val tanggalPost: String? = null,

	@SerializedName("deskripsi")
	val deskripsi: String? = null,

	@SerializedName("judulPost")
	val judulPost: String? = null,

	@SerializedName("upvote")
	val upvote: Int? = null,

	@SerializedName("user")
	val user: User2,

	@SerializedName("email")
	val email: String? = null,

	@SerializedName("idPost")
	val idPost: Int? = null,

	var likeCount: Int = 0,
	var isLiked: Boolean = false,

): Serializable

data class User2(
	@SerializedName("user")
	val listReply: List<Any?>? = null,

	@SerializedName("idUser")
	val idUser: Int? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("jenisKelamin")
	val jenisKelamin: String? = null
)

data class User3(
	@SerializedName("idUser")
	val idUser: Int? = null,
)
