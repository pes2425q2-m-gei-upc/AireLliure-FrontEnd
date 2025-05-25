// File: com/front_pes/heatmap/CustomHeatmapTileProvider.kt
package com.front_pes.features.screens.map

import android.graphics.*
import android.util.Log
import com.front_pes.SelectedContaminants
import com.front_pes.features.screens.map.EstacioQualitatAireResponse
import com.front_pes.network.RetrofitClient.apiService
import com.front_pes.utils.calculateAlphaForZoom
import com.front_pes.utils.getColorForIndex
import com.front_pes.utils.latLngToPixelXY
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import java.io.ByteArrayOutputStream
import kotlin.math.cos
import kotlin.math.PI

class CustomHeatmapTileProvider(
    private val stations: List<EstacioQualitatAireResponse>,
    private val averages: Map<Int, Double>,
    private val radiusMeters: Float = 2000f
) : TileProvider {

    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        val tileSize = 256
        val highResMultiplier = 2 // Generar a 512x512
        val highResSize = tileSize * highResMultiplier

        val dynamicAlpha = calculateAlphaForZoom(zoom)

        val highResBitmap = Bitmap.createBitmap(highResSize, highResSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(highResBitmap)

        val scaleFactor = highResSize / tileSize.toFloat()

        stations.forEach { station ->

            val avgValue = averages[station.id] ?: 0.0
            Log.d("Average", "Station ${station.id} → avg = $avgValue")

            val stationColor = getColorForIndex(avgValue)

            val globalPoint = latLngToPixelXY(station.latitud, station.longitud, zoom)
            val tileOriginX = x * tileSize * scaleFactor
            val tileOriginY = y * tileSize * scaleFactor
            val localX = globalPoint.x * scaleFactor - tileOriginX
            val localY = globalPoint.y * scaleFactor - tileOriginY

            val resolution = (156543.03392 * cos(station.latitud * PI / 180)) / (1 shl zoom)
            val pixelRadius = (radiusMeters / resolution).toFloat() * scaleFactor

            val gradient = RadialGradient(
                localX, localY, pixelRadius,
                intArrayOf(
                    adjustAlpha(stationColor, dynamicAlpha),
                    adjustAlpha(stationColor, dynamicAlpha),
                    adjustAlpha(stationColor, (dynamicAlpha * 0.5).toInt()),
                    adjustAlpha(stationColor, 0)
                ),
                floatArrayOf(0.0f, 0.0f, 0.7f, 1.0f),
                Shader.TileMode.CLAMP
            )


            val paint = Paint().apply {
                isAntiAlias = true
                shader = gradient
                xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
            }

            canvas.drawCircle(localX, localY, pixelRadius, paint)
        }

        val scaledBitmap = Bitmap.createScaledBitmap(highResBitmap, tileSize, tileSize, true)
        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Tile(tileSize, tileSize, byteArray)
    }


    private fun adjustAlpha(color: Int, factor: Int): Int {
        val alpha = factor.coerceIn(0, 255)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }
}
