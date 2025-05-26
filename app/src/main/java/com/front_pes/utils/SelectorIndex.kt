// ktlint-disable
@file:Suppress("ALL")
package com.front_pes.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.features.screens.map.RutaAmbPunt

object SelectorIndex {
    var selectedIndex by mutableStateOf(-1)
    var selectedFiltre by mutableStateOf(-1)
    var selectedEstacio by mutableStateOf<EstacioQualitatAireResponse?>(null)
    var selectedRuta by mutableStateOf<RutaAmbPunt?>(null)
}
