package com.front_pes.features.screens.xats

import com.google.gson.annotations.SerializedName

data class GroupUpdateRequest(
    val nom: String,
    val creador: String,
    @SerializedName("descripció") val descripcio: String,
    val membres: List<String>
)