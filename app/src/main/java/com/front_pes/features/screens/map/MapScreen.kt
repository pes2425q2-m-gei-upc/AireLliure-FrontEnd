package com.front_pes.features.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

const val MapScreenDestination = "Map"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var showPermissionRequest by remember { mutableStateOf(false) }

    // Lista mutable para estaciones
    val estacions = remember { mutableStateListOf<EstacioQualitatAireResponse>() }

    val rutes = remember { mutableStateListOf<RutasResponse>() }


    val cameraPositionState = rememberCameraPositionState()

    val plazaCatalunya = LatLng(41.3825, 2.1912)

    var selectedEstacio by remember { mutableStateOf<EstacioQualitatAireResponse?>(null) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    // Obtener estaciones de calidad del aire
    LaunchedEffect(Unit) {
        viewModel.fetchEstacionsQualitatAire(
            onSuccess = { estaciones ->
                estacions.clear()
                estacions.addAll(estaciones)
            },
            onError = { errorMessage -> }
        )
        // Obtener rutas
        viewModel.fetchRutes(
            onSuccess = { rutesList ->
                rutes.clear()
                rutes.addAll(rutesList)
                rutesList.forEach { ruta ->
                    android.util.Log.d("MAP_SCREEN", "Ruta ID: ${ruta.id}")
                }
            },
            onError = { errorMsg ->
                android.util.Log.e("MAP_SCREEN", "Error cargando rutas: $errorMsg")
            }
        )
    }

    // Verificar permisos y obtener la ubicación del usuario
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                locationPermissionGranted = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        val newLocation = LatLng(it.latitude, it.longitude)
                        userLocation = newLocation
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 16f)
                    }
                }
            }
            else -> {
                locationPermissionGranted = false
                showPermissionRequest = true
                cameraPositionState.position = CameraPosition.fromLatLngZoom(plazaCatalunya, 16f)
            }
        }
    }

    if (isBottomSheetVisible && selectedEstacio != null) {
        ModalBottomSheet(
            onDismissRequest = { isBottomSheetVisible = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = selectedEstacio!!.nom_estacio,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Índice de calidad del aire: ${selectedEstacio!!.index_qualitat_aire}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { isBottomSheetVisible = false },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cerrar")
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionGranted)
        ) {
            // Dibujar Markers solo si hay estaciones disponibles
            estacions.forEach { estacio ->
                Marker(
                    state = MarkerState(
                        position = LatLng(estacio.latitud, estacio.longitud)
                    ),
                    title = estacio.nom_estacio,
                    snippet = "Índice de calidad del aire: ${estacio.index_qualitat_aire}",
                    onClick = {
                        selectedEstacio = estacio
                        isBottomSheetVisible = true
                        true
                    }
                )
            }
        }

        // Mensaje de permisos si no están concedidos
        if (showPermissionRequest) {
            Text(
                text = "Para acceder a tu ubicación, por favor otórganos los permisos.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                    .padding(16.dp)
            )
        }
    }
}
