// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.register

data class RegisterResponse(
    val correu: String,
    val password: String,
    val nom: String,
    val about: String,
    val estat: String,
    val punts: Int
)
