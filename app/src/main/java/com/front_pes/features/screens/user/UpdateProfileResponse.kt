@file:Suppress("detekt")
package com.front_pes.features.screens.user

import android.net.Uri

data class UpdateProfileResponse(
    val nom: String,
    val about: String,
    val correu: String,
    val estat: String,
    val punts: Int,
    val imatge: String

)