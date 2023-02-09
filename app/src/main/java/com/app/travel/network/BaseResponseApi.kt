package com.app.travel.network

import com.google.gson.annotations.SerializedName

data class BaseResponseApi(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("trace_code")
    val traceCode: String? = null,

    @field:SerializedName("message")
    val message: String? = null
)