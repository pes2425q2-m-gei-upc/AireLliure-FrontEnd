package com.front_pes.features.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.front_pes.R
import com.front_pes.features.screens.settings.LanguageViewModel
import com.front_pes.getString
import com.front_pes.utils.SelectorIndex
import com.front_pes.utils.SelectorIndex.selectedFiltre
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

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

    val estacions by viewModel.estacions.collectAsState()
    val rutesAmbPunt by viewModel.rutesAmbPunt.collectAsState()
    val activitats by viewModel.activitats.collectAsState()


    val cameraPositionState = rememberCameraPositionState()
    val plazaCatalunya = LatLng(41.3825, 2.1912)

    var selectedEstacio by remember { mutableStateOf<EstacioQualitatAireResponse?>(null) }
    var selectedRuta by remember { mutableStateOf<RutaAmbPunt?>(null) }
    var selectedActivitat by remember { mutableStateOf<ActivitatCulturalResponse?>(null) }

    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val languageViewModel: LanguageViewModel = viewModel()
    val selectedLanguage by languageViewModel.selectedLanguage.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()

    var isTracking = viewModel.isTracking
    var totalDistance = viewModel.totalDistance;
    var previousLocation by remember { mutableStateOf<Location?>(null) }

    val locationRequest = remember {
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateDistanceMeters(1f)
            .build()
    }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                if (previousLocation != null) {
                    val dist = previousLocation!!.distanceTo(loc)
                    viewModel.totalDistance += dist
                }
                previousLocation = loc
            }
        }
    }

    val currentLocation = userLocation
    val currentRuta = selectedRuta

    val distanciaARuta by remember(currentLocation, selectedRuta) {
        mutableStateOf(
            if (currentLocation != null && selectedRuta != null) {
                FloatArray(1).also {
                    if (currentRuta != null) {
                        Location.distanceBetween(
                            currentLocation.latitude,
                            currentLocation.longitude,
                            currentRuta.punt.latitud,
                            currentRuta.punt.longitud,
                            it
                        )
                    }
                }[0]
            } else Float.MAX_VALUE
        )
    }

    val puedeRecorrer = distanciaARuta <= 1000f

    LaunchedEffect(isTracking) {
        if (isTracking) {
            totalDistance = 0f
            previousLocation = null
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

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

    LaunchedEffect(estacions, reloadTrigger) {
        if (estacions.isNotEmpty()) {
            averagesReady = false
            viewModel.fetchAveragesForStations(estacions) {
                averagesReady = true
            }
        }
    }

    // Cargar datos de la API
    LaunchedEffect(Unit) {
        viewModel.fetchEstacionsQualitatAire(
            onError = { Log.e("MAP_SCREEN", it) }
        )
        viewModel.fetchRutes(
            onError = { Log.e("MAP_SCREEN", it) }
        )
        viewModel.fetchActivitatsCulturals(
            onError = { Log.e("MAP_SCREEN", it) }
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
    LaunchedEffect(SelectorIndex.selectedEstacio) {
        SelectorIndex.selectedEstacio?.let { estacio ->
            Log.d("MAP", "MapScreen: Estación recibida → ${estacio.nom_estacio}")

            selectedEstacio = estacio
            selectedRuta = null
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(estacio.latitud, estacio.longitud),
                16f // Zoom que tú quieras
            )
            SelectorIndex.selectedEstacio = null
        }
    }

    LaunchedEffect(SelectorIndex.selectedRuta) {
        SelectorIndex.selectedRuta?.let { ruta ->
            Log.d("MAP", "MapScreen: Ruta recibida → ${ruta.ruta.nom}")

            selectedRuta = ruta
            selectedEstacio = null
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(ruta.punt.latitud, ruta.punt.longitud),
                16f
            )
            SelectorIndex.selectedRuta = null
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {
        // Modal inferior
        if (isBottomSheetVisible && (selectedEstacio != null || selectedRuta != null || selectedActivitat != null)) {
            ModalBottomSheet(
                onDismissRequest = {
                    isBottomSheetVisible = false
                    selectedEstacio = null
                    selectedRuta = null
                    selectedActivitat = null
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
                            text = (getString(context, R.string.contmed, selectedLanguage)),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Recuperamos la lista de PresenciaResponse
                        val presencias = viewModel.valuesMap[it.id] ?: emptyMap()
                        if (presencias.isEmpty()) {
                            Text(
                                text = (getString(context, R.string.nodatos, selectedLanguage)),
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

                        val distIndex = lines.indexOfFirst { line ->
                            line.startsWith("Distància:")
                        }

                        val displayLines = if (distIndex >= 0) {
                            lines.subList(0, distIndex + 1)
                        } else {
                            lines
                        }

                        Column {
                            Text(it.ruta.nom, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            // Descripción dividida
                            displayLines.forEach { line ->
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
                            Text(text = getString(context, R.string.vermas, selectedLanguage))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (!isTracking && puedeRecorrer) {
                                    Button(
                                        onClick = {
                                            viewModel.startTracking()
                                            viewModel.targetDistance = lines
                                                .firstOrNull { it.contains("Distància:") }
                                                ?.substringAfter("Distància:")
                                                ?.filter { it.isDigit() || it == '.' }
                                                ?.replace(".", "")
                                                ?.toFloatOrNull() ?: 0f
                                            viewModel.nomRutaRecorreguda = selectedRuta!!.ruta.id.toString()
                                            isBottomSheetVisible = false
                                        }

                                    ) {
                                        Text(text = if (isTracking) "Detener y resetear" else "Recorrer ruta")
                                    }
                                }
                            }
                        }
                    }
                    selectedActivitat?.let {
                        Text(it.nom_activitat, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = it.descripcio,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = "Del ${it.data_inici} al ${it.data_fi}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
        // Mapa
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
        ) {


            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
                contentPadding = PaddingValues(top = 8.dp)
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
                    val drawable = AppCompatResources.getDrawable(context, R.drawable.img_6)
                    val original = drawable?.toBitmap()
                    val scaled = original?.scale(84, 84)
                    val iconSized = scaled?.let { BitmapDescriptorFactory.fromBitmap(it) }
                    rutesAmbPunt.forEach { (ruta, punt) ->
                        Marker(
                            state = MarkerState(position = LatLng(punt.latitud, punt.longitud)),
                            title = ruta.nom,
                            icon = iconSized,
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
                    val drawable = AppCompatResources.getDrawable(context, R.drawable.img_6)
                    val original = drawable?.toBitmap()
                    val scaled = original?.scale(84, 84)
                    val iconSized = scaled?.let { BitmapDescriptorFactory.fromBitmap(it) }
                    rutesAmbPunt.forEach { (ruta, punt) ->
                        Marker(
                            state = MarkerState(position = LatLng(punt.latitud, punt.longitud)),
                            title = ruta.nom,
                            icon = iconSized,
                            onClick = {
                                selectedRuta = RutaAmbPunt(ruta, punt)
                                isBottomSheetVisible = true
                                true
                            }
                        )
                    }


                    val drawableAct = AppCompatResources.getDrawable(context, R.drawable.fb36) // tu icono
                    val originalAct = drawableAct?.toBitmap()
                    val scaledAct = originalAct?.scale(84, 84)
                    val iconAct = scaledAct?.let { BitmapDescriptorFactory.fromBitmap(it) }

                    activitats.forEach { activitat ->
                        Marker(
                            state = MarkerState(position = LatLng(activitat.latitud, activitat.longitud)),
                            title = activitat.nom_activitat,
                            icon = iconAct,
                            onClick = {
                                Log.d("MAP_SCREEN", "Clic en activitat: ${activitat.nom_activitat}")
                                selectedActivitat = activitat
                                selectedEstacio = null
                                selectedRuta = null
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
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
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