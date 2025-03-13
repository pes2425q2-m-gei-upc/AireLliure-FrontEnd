package com.front_pes.features.screens.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val correu: String,
    val password: String
)