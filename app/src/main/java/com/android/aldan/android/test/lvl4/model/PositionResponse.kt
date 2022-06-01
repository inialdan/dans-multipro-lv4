package com.android.aldan.android.test.lvl4.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PositionResponse(
    @field:SerializedName("")
    val data: List<PositionResponseItem?>? = null,

    @field:SerializedName("error")
    val error: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,
) : Parcelable

@Parcelize
data class PositionResponseItem(

    @field:SerializedName("company_logo")
    val companyLogo: String? = null,

    @field:SerializedName("how_to_apply")
    val howToApply: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("company")
    val company: String? = null,

    @field:SerializedName("company_url")
    val companyUrl: String? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("url")
    val url: String? = null
) : Parcelable
