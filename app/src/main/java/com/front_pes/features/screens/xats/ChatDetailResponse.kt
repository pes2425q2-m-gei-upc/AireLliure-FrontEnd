@file:Suppress("detekt")
package com.front_pes.features.screens.xats

data class ChatDetailResponse(
    val xat: XatInfo,
    val missatges: List<ChatMessage>
)

data class XatInfo(
    val id: Int
)

data class ChatMessage(
    val id: Int,
    val text: String,
    val data: String,
    val xat: Int,
    val autor: String?,
    val nom: String?
)
