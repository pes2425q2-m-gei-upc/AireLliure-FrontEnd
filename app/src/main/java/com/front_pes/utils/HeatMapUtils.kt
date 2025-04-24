// File: com/front_pes/utils/HeatmapUtils.kt
package com.front_pes.utils

import android.graphics.Color
import android.graphics.PointF
import kotlin.math.*

fun latLngToPixelXY(lat: Double, lng: Double, zoom: Int): PointF {
    val sinLat = sin(lat * PI / 180.0)
    val mapSize = 256 * (1 shl zoom) // 256 * 2^zoom
    val x = ((lng + 180.0) / 360.0 * mapSize).toFloat()
    val y = ((0.5 - ln((1 + sinLat) / (1 - sinLat)) / (4 * PI)) * mapSize).toFloat()
    return PointF(x, y)
}

fun getColorForIndex(index: Double): Int {
    return when {
        index.isNaN() -> Color.TRANSPARENT
        index <= 0.25 -> Color.RED
        index <= 0.5 -> Color.parseColor("#FFA500")
        index <= 0.75 -> Color.YELLOW
        else -> Color.GREEN
    }
}

fun calculateAlphaForZoom(zoom: Int): Int {
    return when {
        zoom >= 16 -> 30
        zoom in 14..15 -> 40
        zoom in 13..14 -> 80
        zoom in 12..13 -> 100
        zoom in 11..12 -> 140
        zoom in 10..11 -> 180
        else -> 255
    }
}
