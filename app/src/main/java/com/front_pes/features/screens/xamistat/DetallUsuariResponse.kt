// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xamistat

data class DetallUsuariResponse(
    val correu: String,
    val password: String?=null,
    val nom: String?=null,
    val estat: String?=null,
    val punts: Int?=null,
    val deshabilitador: String?=null,
    val about: String?=null,
    val imatge: String?=null
)
