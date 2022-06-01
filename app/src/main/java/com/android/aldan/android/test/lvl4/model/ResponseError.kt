package com.android.aldan.android.test.lvl4.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseError (

    @field:SerializedName("error")
    val error: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("success")
    val success: Boolean? = null,
) : Parcelable