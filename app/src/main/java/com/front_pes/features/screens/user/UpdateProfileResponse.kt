package com.front_pes.features.screens.user

data class UpdateProfileResponse(
    val nom: String,
    val about: String,
    val correu: String,
    val estat: String,
    val punts: Int
)