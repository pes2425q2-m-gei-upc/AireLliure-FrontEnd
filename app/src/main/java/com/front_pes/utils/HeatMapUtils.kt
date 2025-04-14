// File: com/front_pes/utils/HeatmapUtils.kt
package com.front_pes.utils

import android.graphics.Color
import android.graphics.PointF
import kotlin.math.*

// Función para convertir latitud y longitud a coordenadas globales (en píxeles) usando proyección Mercator.
fun latLngToPixelXY(lat: Double, lng: Double, zoom: Int): PointF {
    val sinLat = sin(lat * PI / 180.0)
    val mapSize = 256 * (1 shl zoom) // 256 * 2^zoom
    val x = ((lng + 180.0) / 360.0 * mapSize).toFloat()
    val y = ((0.5 - ln((1 + sinLat) / (1 - sinLat)) / (4 * PI)) * mapSize).toFloat()
    return PointF(x, y)
}

// Función para asignar el color basado en el índice de calidad del aire.
// Ejemplo: rojo para valores bajos, amarillo para intermedios y verde para valores altos.
fun getColorForIndex(index: Double): Int {
    return when {
        index <= 0.3 -> Color.RED
        index <= 0.6 -> Color.YELLOW
        else -> Color.GREEN
    }
}

fun calculateAlphaForZoom(zoom: Int): Int {
    return when {
        zoom >= 16 -> 20
        zoom in 12..15 -> 40
        zoom in 10..13 -> 80
        else -> 120
    }
}