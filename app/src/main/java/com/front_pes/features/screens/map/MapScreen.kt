package com.front_pes.features.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.front_pes.utils.SelectorIndex



const val MapScreenDestination = "Map"

data class RutaAmbPunt(
    val ruta: RutasResponse,
    val punt: PuntsResponse
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel(), title: String) {

    val selectedIndex by remember { derivedStateOf { SelectorIndex.selectedIndex } }


    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var showPermissionRequest by remember { mutableStateOf(false) }

    val estacions = remember { mutableStateListOf<EstacioQualitatAireResponse>() }
    val rutesAmbPunt = remember { mutableStateListOf<RutaAmbPunt>() }

    val cameraPositionState = rememberCameraPositionState()
    val plazaCatalunya = LatLng(41.3825, 2.1912)

    var selectedEstacio by remember { mutableStateOf<EstacioQualitatAireResponse?>(null) }
    var selectedRuta by remember { mutableStateOf<RutaAmbPunt?>(null) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    // Cargar datos de la API
    LaunchedEffect(Unit) {
        viewModel.fetchEstacionsQualitatAire(
            onSuccess = { estaciones ->
                estacions.clear()
                estacions.addAll(estaciones)
            },
            onError = { errorMessage -> }
        )

        viewModel.fetchRutes(
            onSuccess = { rutesList ->
                rutesList.forEach { ruta ->
                    ruta.punt_inici?.let { puntId ->
                        viewModel.fetchPuntByID(
                            pk = puntId,
                            onSuccess = { punt ->
                                rutesAmbPunt.add(RutaAmbPunt(ruta = ruta, punt = punt))
                            },
                            onError = { errorMsg ->
                                Log.e("MAP_SCREEN", "Error obteniendo punt_inici: $errorMsg")
                            }
                        )
                    }
                }
            },
            onError = { errorMsg ->
                Log.e("MAP_SCREEN", "Error cargando rutas: $errorMsg")
            }
        )
    }

    // Ubicación del usuario
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

    // Modal inferior
    if (isBottomSheetVisible && (selectedEstacio != null || selectedRuta != null)) {
        ModalBottomSheet(
            onDismissRequest = {
                isBottomSheetVisible = false
                selectedEstacio = null
                selectedRuta = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                selectedEstacio?.let {
                    Text(it.nom_estacio, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Índice de calidad del aire: ${it.index_qualitat_aire}", style = MaterialTheme.typography.bodyLarge)
                }

                selectedRuta?.let {
                    Text(it.ruta.nom, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Distancia: ${it.ruta.dist_km} km", style = MaterialTheme.typography.bodyLarge)
                }

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

    // Mapa
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = locationPermissionGranted)
        ) {
            if (selectedIndex == 0) {
                // Solo estaciones
                estacions.forEach { estacio ->
                    Marker(
                        state = MarkerState(position = LatLng(estacio.latitud, estacio.longitud)),
                        title = estacio.nom_estacio,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        onClick = {
                            selectedEstacio = estacio
                            isBottomSheetVisible = true
                            true
                        }
                    )
                }
            } else if (selectedIndex == 1) {
                // Solo rutas
                rutesAmbPunt.forEach { (ruta, punt) ->
                    Marker(
                        state = MarkerState(position = LatLng(punt.latitud, punt.longitud)),
                        title = ruta.nom,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        onClick = {
                            selectedRuta = RutaAmbPunt(ruta, punt)
                            isBottomSheetVisible = true
                            true
                        }
                    )
                }
            } else {
                // Ambas
                estacions.forEach { estacio ->
                    Marker(
                        state = MarkerState(position = LatLng(estacio.latitud, estacio.longitud)),
                        title = estacio.nom_estacio,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                        onClick = {
                            selectedEstacio = estacio
                            isBottomSheetVisible = true
                            true
                        }
                    )
                }
                rutesAmbPunt.forEach { (ruta, punt) ->
                    Marker(
                        state = MarkerState(position = LatLng(punt.latitud, punt.longitud)),
                        title = ruta.nom,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        onClick = {
                            selectedRuta = RutaAmbPunt(ruta, punt)
                            isBottomSheetVisible = true
                            true
                        }
                    )
                }
            }
        }

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
