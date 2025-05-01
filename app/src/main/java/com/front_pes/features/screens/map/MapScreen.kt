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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.front_pes.utils.SelectorIndex
import com.front_pes.utils.SelectorIndex.selectedFiltre

const val MapScreenDestination = "Map"

data class RutaAmbPunt(
    val ruta: RutasResponse,
    val punt: PuntsResponse
)

val idToContaminantName = mapOf(
    2 to "NO2",
    3 to "O3",
    4 to "PM10",
    41091 to "H2S",
    41092 to "NO",
    41093 to "SO2",
    41096 to "PM2.5",
    41097 to "NOX",
    41098 to "CO",
    41100 to "C6H6",
    41101 to "PM1",
    41102 to "Hg"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel(), onRutaClick: (Int) -> Unit, title: String, reloadTrigger: Boolean = false) {

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
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(newLocation, 0f)
                }
            }
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(plazaCatalunya, 4f)
            if (!viewModel.hasShownPermissionWarning) {
                showLocationDeniedDialog = true
                viewModel.hasShownPermissionWarning = true
            }
        }
    }

    var averagesReady by remember { mutableStateOf(false) }

    LaunchedEffect(reloadTrigger) {
        if (estacions.isNotEmpty()) {
            averagesReady = false
            viewModel.fetchAveragesForStations(estacions) {
                averagesReady = true
            }
            Log.d("LoadingMapScreen", "MapScreen cargado o recargado")
        }
    }

    // Cargar datos de la API
    LaunchedEffect(Unit) {
        viewModel.fetchEstacionsQualitatAire(
            onSuccess = { estaciones ->
                estacions.clear()
                estacions.addAll(estaciones)

                averagesReady = false
                viewModel.fetchAveragesForStations(estaciones) {
                    averagesReady = true
                    Log.d("MAP_SCREEN", "Averages cargados correctamente")
                }
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
                        Text(it.nom_estacio, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        val averageValue = viewModel.averageMap[it.id]
                        Text(
                            text = getString(context, R.string.icalidad, selectedLanguage) + ": " +
                                    (averageValue?.toString() ?: "N/A"),
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Contaminantes medidos:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Recuperamos la lista de PresenciaResponse
                        val presencias = viewModel.valuesMap[it.id] ?: emptyMap()
                        if (presencias.isEmpty()) {
                            Text(
                                text = "No hay datos disponibles.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            presencias.forEach { (contaminantId, averageValue) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val contaminantName = idToContaminantName[contaminantId] ?: contaminantId.toString()

                                    Text(
                                        text = contaminantName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = if (!averageValue.isNaN()) String.format("%.2f", averageValue) else "–",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    selectedRuta?.let {
                        val raw = it.ruta.descripcio
                        val lines = raw
                            .replace("</p>", "")
                            .split("<p>")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }

                        Column {
                            Text(it.ruta.nom, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
//                            // Distancia
//                            Text(
//                                text = getString(context, R.string.dist, selectedLanguage) + ": ${it.ruta.dist_km} km",
//                                style = MaterialTheme.typography.bodyLarge
//                            )
                            Spacer(Modifier.height(8.dp))
                            // Descripción dividida
                            lines.forEach { line ->
                                Text(
                                    text = line,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        Button(
                            onClick = {
                                println(it.ruta.id)
                                onRutaClick(it.ruta.id)
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
                if (selectedFiltre == 0) {
                    estacions.forEach { estacio ->
                        Marker(
                            state = MarkerState(position = LatLng(estacio.latitud, estacio.longitud)),
                            title = estacio.nom_estacio,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                            onClick = {
                                selectedEstacio = estacio
                                isBottomSheetVisible = true
                                true
                            }
                        )
                    }
                } else if (selectedFiltre == 1) {
                    rutesAmbPunt.forEach { (ruta, punt) ->
                        Marker(
                            state = MarkerState(position = LatLng(punt.latitud, punt.longitud)),
                            title = ruta.nom,
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
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
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
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
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                            onClick = {
                                selectedRuta = RutaAmbPunt(ruta, punt)
                                isBottomSheetVisible = true
                                true
                            }
                        )
                    }
                }

                MapEffect(key1 = estacions.toList(), key2 = averagesReady) { googleMap ->
                    if (estacions.isNotEmpty()) {
                        Log.d("Testing", "averageMap desde fuera: ${viewModel.averageMap}")
                        val tileProvider = CustomHeatmapTileProvider(stations = estacions, averages = viewModel.averageMap)
                        googleMap.addTileOverlay(
                            com.google.android.gms.maps.model.TileOverlayOptions()
                                .tileProvider(tileProvider)
                                .zIndex(1f)
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