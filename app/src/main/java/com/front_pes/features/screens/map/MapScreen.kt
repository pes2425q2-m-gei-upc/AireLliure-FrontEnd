package com.front_pes.features.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.front_pes.utils.SelectorIndex

const val MapScreenDestination = "Map"

data class RutaAmbPunt(
    val ruta: RutasResponse,
    val punt: PuntsResponse
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    title: String,
    onNavigateToDetail: (String) -> Unit,
    RutaViewModel: RutaViewModel
) {

    val selectedIndex by remember { derivedStateOf { SelectorIndex.selectedIndex } }

    val context = androidx.compose.ui.platform.LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var locationPermissionGranted by remember { mutableStateOf(false) }
    var showLocationDeniedDialog by remember { mutableStateOf(false) }

    val estacions = remember { mutableStateListOf<EstacioQualitatAireResponse>() }
    val rutesAmbPunt = remember { mutableStateListOf<RutaAmbPunt>() }

    val cameraPositionState = rememberCameraPositionState()
    val plazaCatalunya = LatLng(41.3825, 2.1912)

    var selectedEstacio by remember { mutableStateOf<EstacioQualitatAireResponse?>(null) }
    var selectedRuta by remember { mutableStateOf<RutaAmbPunt?>(null) }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        locationPermissionGranted = isGranted
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    userLocation = newLocation
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 16f)
                }
            }
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(plazaCatalunya, 16f)
            if (!viewModel.hasShownPermissionWarning) {
                showLocationDeniedDialog = true
                viewModel.hasShownPermissionWarning = true
            }
        }
    }

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

    // Solicitar permiso de ubicación una sola vez por sesión
    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    userLocation = newLocation
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 16f)
                }
            }
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(plazaCatalunya, 16f) // ✅ Siempre mover la cámara

            if (!viewModel.alreadyAskedPermission) {
                viewModel.alreadyAskedPermission = true
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                if (!viewModel.hasShownPermissionWarning) {
                    viewModel.hasShownPermissionWarning = true
                    showLocationDeniedDialog = true
                }
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
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
                        Text(text = getString(context, R.string.icalidad, selectedLanguage) + ": ${it.index_qualitat_aire}", style = MaterialTheme.typography.bodyLarge)
                    }

                    selectedRuta?.let {
                        Text(it.ruta.nom, style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = getString(context, R.string.dist, selectedLanguage) + ": ${it.ruta.dist_km} km", style = MaterialTheme.typography.bodyLarge)
                        Button(
                            onClick = {
                                RutaViewModel.setRuta(it.ruta)
                                onNavigateToDetail(it.ruta.id)
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Ver más")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { isBottomSheetVisible = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = getString(context, R.string.cerrar, selectedLanguage))
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
        }

        // Aviso si denegó el permiso (una vez por sesión)
        if (showLocationDeniedDialog) {
            AlertDialog(
                onDismissRequest = { showLocationDeniedDialog = false },
                title = { Text(text = getString(context, R.string.u_perm, selectedLanguage)) },
                text = { Text(text = getString(context, R.string.hab, selectedLanguage)) },
                confirmButton = {
                    TextButton(onClick = { showLocationDeniedDialog = false }) {
                        Text(text = getString(context, R.string.aceptar, selectedLanguage))
                    }
                }
            )
        }
    }
}
