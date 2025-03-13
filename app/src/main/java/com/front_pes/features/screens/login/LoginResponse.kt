package com.front_pes.features.screens.login

data class LoginResponse(
    val correu: String,
    val password: String,
    val nom: String,
    val estat: String,
    val punts: Int
)
