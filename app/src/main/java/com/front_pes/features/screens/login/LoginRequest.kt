@file:Suppress("detekt")
package com.front_pes.features.screens.login

data class LoginRequest(
    val correu: String,
    val password: String
)