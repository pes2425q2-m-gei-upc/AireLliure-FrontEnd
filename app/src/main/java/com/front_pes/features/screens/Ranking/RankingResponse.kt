package com.front_pes.features.screens.Ranking

data class RankingResponse(
    val correu: String,
    val password: String,
    val nom: String,
    val estat: String,
    val punts: Int,
    val deshabilitador: String?,
    val about: String? = null
)
