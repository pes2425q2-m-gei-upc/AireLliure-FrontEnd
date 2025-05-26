// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.administrador

data class HabResponse(
    val correu: String,
    val password: String,
    val nom: String,
    val estat: String,
    val punts: Int,
    val deshabilitador: String?,
    val about: String? = null,
    val administrador: Boolean
)
