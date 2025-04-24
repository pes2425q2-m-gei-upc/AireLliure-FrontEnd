package com.front_pes.features.screens.map

data class GetPresenciaRequest(
    val selected: Set<String>
) {
    fun toQueryMap(): Map<String, Boolean> =
        selected.associateWith { true }
}
