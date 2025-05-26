// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.features.screens.xats

data class UpdateMessageResponse(
    val id: Int,
    val text: String,
    val data: String,
    val xat: Int,
    val autor: String
)
