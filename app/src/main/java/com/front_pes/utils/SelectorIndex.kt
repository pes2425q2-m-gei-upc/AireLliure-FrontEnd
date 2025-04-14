package com.front_pes.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object SelectorIndex {
    var selectedIndex by mutableStateOf(-1)
    var selectedFiltre by mutableStateOf(-1)
}
