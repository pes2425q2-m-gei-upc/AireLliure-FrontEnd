package com.front_pes.features.screens.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val success: Boolean, val token: String?)
