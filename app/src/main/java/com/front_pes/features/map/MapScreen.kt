package com.front_pes.features.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ModifierInfo
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.lifecycle.viewmodel.compose.viewModel


//Per posar una posició inicial al mapa
private const val INITIAL_LATITUDE = 41.387671
private const val INITIAL_LONGITUDE = 2.112950
private const val INITIAL_ZOOM_LEVEL = 16f

const val MapScreenDestination = "Map"

@Composable

fun MapScreen(viewModel: MapViewModel = viewModel()) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(INITIAL_LATITUDE, INITIAL_LONGITUDE),
            INITIAL_ZOOM_LEVEL,
        )
    }
    GoogleMap( cameraPositionState = cameraPositionState)
}