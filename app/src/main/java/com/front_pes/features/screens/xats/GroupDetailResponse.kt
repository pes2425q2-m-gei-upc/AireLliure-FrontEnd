@file:Suppress("detekt")
package com.front_pes.features.screens.xats
import com.google.gson.annotations.SerializedName

data class GroupDetailResponse(
    val id: Int,
    val nom: String,
    @SerializedName("descripció") val descripcio: String,
    val creador: String,
    val membres: List<String>
)
