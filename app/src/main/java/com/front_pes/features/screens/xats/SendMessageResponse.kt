@file:Suppress("detekt")
package com.front_pes.features.screens.xats

data class SendMessageResponse(
    val id: Int,
    val text: String,
    val data: String,
    val xat: Int,
    val autor: String
)
