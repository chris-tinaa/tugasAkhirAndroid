package com.bcaf.inovative.data.api.request

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GetTop10Post(

	@SerializedName("data")
	val data: DataTop10? = null,

	@SerializedName("success")
	val success: Boolean? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: Int? = null,

	@SerializedName("timestamp")
	val timestamp: String? = null
)

@Parcelize
data class DataTop10(
	val numberOfElements: Int? = null,
	val totalPages: Int? = null,
	val currentPage: Int? = null,
	val content: List<DataItem?>? = null
) : Parcelable
