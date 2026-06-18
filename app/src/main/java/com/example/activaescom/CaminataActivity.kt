package com.example.activaescom

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.activaescom.database.entities.CaminataConfiguracionEntity
import com.example.activaescom.database.entities.CaminataDetalleEntity
import com.example.activaescom.viewmodel.CaminataViewModel
import com.example.activaescom.database.AppDatabase
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class CaminataActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var caminataViewModel: CaminataViewModel

    private lateinit var cardDashboardVivo: CardView
    private lateinit var tvFaseEntrenamiento: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvDistancia: TextView
    private lateinit var tvRitmo: TextView
    private lateinit var tvCalorias: TextView
    private lateinit var tvGpsEstado: TextView

    private lateinit var btnIniciarCronometro: MaterialButton
    private lateinit var layoutControlesActivos: LinearLayout
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    private lateinit var botonesPaso: List<Button>
    private lateinit var botonesTerreno: List<Button>
    private lateinit var spinnerAcompanante: Spinner

    private lateinit var switchCalentamiento: SwitchCompat
    private lateinit var layoutDuracionCalentamiento: LinearLayout
    private lateinit var botonesCalentamiento: List<Button>
    private var minutosCalentamiento: Int = 5

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private val routePoints = mutableListOf<LatLng>()
    private var routePolyline: Polyline? = null
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null

    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private var isRunning = false
    private var isWarmingUp = false
    private var warmupLimitSeconds = 0L
    private var workoutSeconds = 0L
    private var totalDistanceMeters = 0.0

    companion object {
        private const val REQUEST_LOCATION = 101
        private const val MAX_JUMP_METERS = 30f
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds++
                if (isWarmingUp) {
                    if (elapsedSeconds >= warmupLimitSeconds) {
                        transicionACaminata()
                    } else {
                        updateTimerUI()
                    }
                } else {
                    workoutSeconds++
                    updateTimerUI()
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entrenamientoId = intent.getIntExtra("ENTRENAMIENTO_ID", -1)

        if (entrenamientoId == -1) {
            Toast.makeText(this, "Primero inicia un nuevo entrenamiento", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, NuevoEntrenamientoActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_caminata)
        caminataViewModel = CaminataViewModel(application)
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        cargarDatosConfiguracion()
        setupOpcionesEntrenamiento()
        setupBotones()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    private fun bindViews() {
        cardDashboardVivo = findViewById(R.id.cardDashboardVivo)
        tvFaseEntrenamiento = findViewById(R.id.tvFaseEntrenamiento)
        tvTimer = findViewById(R.id.tvTimer)
        tvDistancia = findViewById(R.id.tvDistancia)
        tvRitmo = findViewById(R.id.tvRitmo)
        tvCalorias = findViewById(R.id.tvCalorias)
        btnIniciarCronometro = findViewById(R.id.btnIniciarCronometro)
        layoutControlesActivos = findViewById(R.id.layoutControlesActivos)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        tvGpsEstado = findViewById(R.id.tvGpsEstado)
        botonesPaso = listOf(findViewById(R.id.btnPasoPaseo), findViewById(R.id.btnPasoLigero), findViewById(R.id.btnPasoMarcha))
        botonesTerreno = listOf(findViewById(R.id.btnTerrenoAsfalto), findViewById(R.id.btnTerrenoSendero), findViewById(R.id.btnTerrenoCinta))
        switchCalentamiento = findViewById(R.id.switchCalentamiento)
        layoutDuracionCalentamiento = findViewById(R.id.layoutDuracionCalentamiento)
        botonesCalentamiento = listOf(findViewById(R.id.btnCal5), findViewById(R.id.btnCal10), findViewById(R.id.btnCal15))
        spinnerAcompanante = findViewById(R.id.spinnerAcompanante)
    }

    private fun setupOpcionesEntrenamiento() {
        for (btn in botonesPaso) { btn.setOnClickListener { seleccionarChip(btn, botonesPaso) } }
        for (btn in botonesTerreno) { btn.setOnClickListener { seleccionarChip(btn, botonesTerreno) } }

        switchCalentamiento.setOnCheckedChangeListener { _, isChecked ->
            layoutDuracionCalentamiento.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) minutosCalentamiento = 5
        }

        val duraciones = mapOf(R.id.btnCal5 to 5, R.id.btnCal10 to 10, R.id.btnCal15 to 15)
        for (btn in botonesCalentamiento) {
            btn.setOnClickListener {
                minutosCalentamiento = duraciones[btn.id] ?: 5
                seleccionarChip(btn, botonesCalentamiento)
            }
        }

        val opciones = listOf("En solitario", "Con mascota", "En grupo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAcompanante.adapter = adapter
    }

    private fun seleccionarChip(seleccionado: Button, grupo: List<Button>) {
        for (btn in grupo) {
            if (btn == seleccionado) {
                btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_1)
                btn.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_light)
                btn.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            }
        }
    }

    private fun cargarDatosConfiguracion() {
        findViewById<TextView>(R.id.tvConfigRutinaTitulo).text = intent.getStringExtra("NOMBRE_RUTINA") ?: "Caminata Individual"
        findViewById<TextView>(R.id.tvConfigMeta).text = "Meta: ${intent.getStringExtra("META_DISTANCIA") ?: "3.0 km"}"
        findViewById<TextView>(R.id.tvConfigSueno).text = "💤 : ${intent.getIntExtra("HORAS_SUENO", 7)} hrs"
        findViewById<TextView>(R.id.tvConfigLugar).text = "📍 : ${intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Parque"}"
        findViewById<TextView>(R.id.tvConfigDuracion).text = "⏱️ : ${intent.getIntExtra("DURACION_ESTIMADA", 30)} min"
        findViewById<TextView>(R.id.guardarNotas).text = intent.getStringExtra("FECHA_ENTRENAMIENTO") ?: "No especificada"
    }

    private fun setupBotones() {
        btnIniciarCronometro.setOnClickListener { startWorkout() }
        btnPausar.setOnClickListener { if (isRunning) pauseWorkout() else resumeWorkout() }
        btnFinalizar.setOnClickListener {
            Toast.makeText(this, "Guardando mapa y finalizando...", Toast.LENGTH_SHORT).show()
            finishWorkout()
        }
    }

    private fun startWorkout() {
        val entrenamientoId = intent.getIntExtra("ENTRENAMIENTO_ID", -1)
        val intensidadPaso = botonesPaso.firstOrNull { it.backgroundTintList == ContextCompat.getColorStateList(this, R.color.red_1) }?.text?.toString() ?: "Paseo"
        val terreno = botonesTerreno.firstOrNull { it.backgroundTintList == ContextCompat.getColorStateList(this, R.color.red_1) }?.text?.toString() ?: "Asfalto"
        val minutosCalentamientoFinal = if (switchCalentamiento.isChecked) minutosCalentamiento else 0

        val configuracion = CaminataConfiguracionEntity(
            entrenamientoId = entrenamientoId,
            intensidadPaso = intensidadPaso,
            terreno = terreno,
            calentamientoActivo = switchCalentamiento.isChecked,
            minutosCalentamiento = minutosCalentamientoFinal,
            acompanante = spinnerAcompanante.selectedItem.toString()
        )

        lifecycleScope.launch { caminataViewModel.insertarConfiguracion(configuracion) }

        isRunning = true
        btnIniciarCronometro.visibility = View.GONE
        cardDashboardVivo.visibility = View.VISIBLE
        layoutControlesActivos.visibility = View.VISIBLE

        if (switchCalentamiento.isChecked) {
            isWarmingUp = true
            warmupLimitSeconds = minutosCalentamiento * 60L
            elapsedSeconds = 0L
            workoutSeconds = 0L
            totalDistanceMeters = 0.0
            tvFaseEntrenamiento.text = "🔥 CALENTAMIENTO"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            tvTimer.setTextColor(Color.parseColor("#FF8C00"))
        } else {
            isWarmingUp = false
            elapsedSeconds = 0L
            workoutSeconds = 0L
            totalDistanceMeters = 0.0
            tvFaseEntrenamiento.text = "🚶 EN CAMINATA"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
            tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        }

        handler.post(timerRunnable)
        startLocationUpdates()
    }

    private fun transicionACaminata() {
        isWarmingUp = false
        workoutSeconds = 0L

        try {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 300, 150, 300), -1)
            }
        } catch (e: Exception) {}

        tvFaseEntrenamiento.text = "🚶 EN CAMINATA"
        tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))

        totalDistanceMeters = 0.0
        tvDistancia.text = "0.00"
        tvRitmo.text = "--:--"
        tvCalorias.text = "0"
        updateTimerUI()
    }

    private fun pauseWorkout() {
        isRunning = false
        btnPausar.setIconResource(R.drawable.play)
        handler.removeCallbacks(timerRunnable)
        stopLocationUpdates()
    }

    private fun resumeWorkout() {
        isRunning = true
        btnPausar.setIconResource(R.drawable.pause)
        handler.post(timerRunnable)
        startLocationUpdates()
    }

    private fun capturarYGuardarMapa(entrenamientoId: Int, onComplete: (String?) -> Unit) {
        if (googleMap == null) {
            onComplete(null)
            return
        }

        if (routePoints.isNotEmpty()) {
            val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
            for (point in routePoints) {
                builder.include(point)
            }
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
        }

        handler.postDelayed({
            googleMap?.snapshot { bitmap ->
                if (bitmap == null) {
                    onComplete(null)
                    return@snapshot
                }
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val file = File(filesDir, "mapa_caminata_$entrenamientoId.png")
                        val outputStream = FileOutputStream(file)
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        withContext(Dispatchers.Main) { onComplete(file.absolutePath) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) { onComplete(null) }
                    }
                }
            } ?: onComplete(null)
        }, 1000)
    }

    private fun finishWorkout() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        stopLocationUpdates()

        val entrenamientoId = intent.getIntExtra("ENTRENAMIENTO_ID", -1)
        val distanciaKm = totalDistanceMeters / 1000.0
        val ritmo = tvRitmo.text.toString()
        val calorias = tvCalorias.text.toString().toIntOrNull() ?: 0

        val detalle = CaminataDetalleEntity(
            entrenamientoId = entrenamientoId,
            distanciaKm = distanciaKm,
            ritmo = ritmo,
            caloriasQuemadas = calorias,
            duracionSegundos = workoutSeconds
        )

        capturarYGuardarMapa(entrenamientoId) { rutaMapa ->
            lifecycleScope.launch {
                caminataViewModel.insertarDetalle(detalle)

                // ✅ Guardando 'rutaMapa' en la BD
                AppDatabase.getDatabase(this@CaminataActivity)
                    .entrenamientoDao()
                    .actualizarResultados(entrenamientoId, workoutSeconds, calorias, rutaMapa)

                navegarAMain()
            }
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentCaminata) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled = true
        }
        showInitialLocation()
    }

    private fun showInitialLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            tvGpsEstado.text = "📍 GPS activo"
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 16f))
                }
            }
        } else {
            tvGpsEstado.text = "📍 Buscando GPS..."
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4_000L).setMinUpdateIntervalMillis(3_000L).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { processNewLocation(it) }
            }
        }
        fusedLocationClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        locationCallback = null
    }

    private fun processNewLocation(location: Location) {
        val newPoint = LatLng(location.latitude, location.longitude)
        if (!isWarmingUp) {
            lastLocation?.let { prev ->
                val result = FloatArray(1)
                Location.distanceBetween(prev.latitude, prev.longitude, location.latitude, location.longitude, result)
                if (result[0] in 0.5f..MAX_JUMP_METERS) {
                    totalDistanceMeters += result[0]
                    updateStatsUI()
                }
            }
        }
        lastLocation = location
        routePoints.add(newPoint)
        if (routePolyline == null) {
            routePolyline = googleMap?.addPolyline(PolylineOptions().color(Color.RED).width(9f).geodesic(true))
        }
        routePolyline?.points = routePoints
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f))
    }

    private fun updateTimerUI() {
        val segundos = if (isWarmingUp) elapsedSeconds else workoutSeconds
        val h = segundos / 3600
        val m = (segundos % 3600) / 60
        val s = segundos % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    private fun updateStatsUI() {
        val km = totalDistanceMeters / 1000.0
        tvDistancia.text = String.format("%.3f", km)
        if (workoutSeconds > 0 && km > 0) {
            val paceSecPerKm = workoutSeconds.toDouble() / km
            val paceMin = (paceSecPerKm / 60).toInt()
            val paceSec = (paceSecPerKm % 60).toInt()
            tvRitmo.text = String.format("%d'%02d\"", paceMin, paceSec)
            tvCalorias.text = (km * 60).toInt().toString()
        }
    }

    private fun abrirPlataforma(urlWeb: String, packageName: String) {
        val intentApp = packageManager.getLaunchIntentForPackage(packageName)
        if (intentApp != null) {
            startActivity(intentApp)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb)))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showInitialLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        handler.removeCallbacks(timerRunnable)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun navegarAMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}