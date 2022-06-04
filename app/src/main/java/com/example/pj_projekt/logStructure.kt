package com.example.pj_projekt

import com.google.gson.annotations.SerializedName

data class logStructure(
    @SerializedName("username")
    var username: String,
    @SerializedName("dateOpened")
    val dateOpened: String,
    @SerializedName("success")
    val success: Boolean)
