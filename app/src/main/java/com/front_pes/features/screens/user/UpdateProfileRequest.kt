package com.front_pes.features.screens.user

data class UpdateProfileRequest(
    val nom: String? = null,
    val about: String? = null,
    val estat: String? = null,
    val punts: Int? = null
)