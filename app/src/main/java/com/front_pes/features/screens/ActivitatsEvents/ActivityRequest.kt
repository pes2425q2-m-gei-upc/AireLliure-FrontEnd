package com.front_pes.features.screens.ActivitatsEvents

import com.google.gson.annotations.SerializedName

data class ActivityRequest(
    @SerializedName("nom") val nom: String,
    @SerializedName("descripció") val descripcio: String, // <-- con tilde aquí
    @SerializedName("data_inici") val data_inici: String,
    @SerializedName("data_fi") val data_fi: String,
    @SerializedName("creador_event") val creador: String,
    @SerializedName("limit") val limit: Int
)