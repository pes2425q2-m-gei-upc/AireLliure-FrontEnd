package com.front_pes.features.screens.xats

data class GroupCreateRequest(
    val nom: String,
    val creador: String,
    val descripció: String,
    val membres: List<String>
)
