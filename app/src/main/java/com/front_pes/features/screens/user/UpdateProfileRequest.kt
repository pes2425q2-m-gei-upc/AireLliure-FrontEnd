// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.user

import android.net.Uri

data class UpdateProfileRequest(
    val nom: String? = null,
    val about: String? = null,
    val estat: String? = null,
    val punts: Int? = null,
    val imatge: Uri? = null
)