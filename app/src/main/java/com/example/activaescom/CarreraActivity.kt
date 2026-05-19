package com.example.activaescom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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

class CarreraActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout

    // UI del Cronómetro y Estadísticas
    private lateinit var tvTimer: TextView
    private lateinit var tvLiveDistancia: TextView
    private lateinit var tvLiveRitmo: TextView
    private lateinit var btnIniciarCronometro: MaterialButton
    private lateinit var layoutControlesActivos: LinearLayout
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    // Mapa y Ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private val routePoints = mutableListOf<LatLng>()
    private var routePolyline: Polyline? = null
    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null

    // Cronómetro
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private var isRunning = false

    // Estadísticas
    private var totalDistanceMeters = 0.0

    companion object {
        private const val REQUEST_LOCATION = 101
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds++
                updateTimerUI()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrera)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        cargarDatosConfiguracion()
        setupBotones()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    private fun bindViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvLiveDistancia = findViewById(R.id.tvLiveDistancia)
        tvLiveRitmo = findViewById(R.id.tvLiveRitmo)
        btnIniciarCronometro = findViewById(R.id.btnIniciarCronometro)
        layoutControlesActivos = findViewById(R.id.layoutControlesActivos)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
    }

    private fun cargarDatosConfiguracion() {
        val tvConfigRutinaTitulo = findViewById<TextView>(R.id.tvConfigRutinaTitulo)
        val tvConfigMeta = findViewById<TextView>(R.id.tvConfigMeta)
        val tvConfigSueno = findViewById<TextView>(R.id.tvConfigSueno)
        val tvConfigLugar = findViewById<TextView>(R.id.tvConfigLugar)
        val tvConfigDuracion = findViewById<TextView>(R.id.tvConfigDuracion)
        val tvConfigFecha = findViewById<TextView>(R.id.guardarNotas)
        val tvConfigNotas = findViewById<TextView>(R.id.tvConfigNotas)
        val layoutConfigNotas = findViewById<LinearLayout>(R.id.layoutConfigNotas)

        tvConfigRutinaTitulo.text = intent.getStringExtra("NOMBRE_RUTINA") ?: "Carrera Individual"
        tvConfigMeta.text = "Meta: ${intent.getStringExtra("META_DISTANCIA") ?: "5.0 km"}"
        tvConfigSueno.text = "💤 : ${intent.getIntExtra("HORAS_SUENO", 7)} hrs"
        tvConfigLugar.text = "📍 : ${intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Pista"}"
        tvConfigDuracion.text = "⏱️ : ${intent.getIntExtra("DURACION_ESTIMADA", 30)} min"
        tvConfigFecha.text = intent.getStringExtra("FECHA_ENTRENAMIENTO") ?: "No especificada"

        val notasPrevias = intent.getStringExtra("NOTAS") ?: ""
        if (notasPrevias.isNotEmpty()) {
            tvConfigNotas.text = "Notas previas: $notasPrevias"
            layoutConfigNotas.visibility = View.VISIBLE
        } else {
            layoutConfigNotas.visibility = View.GONE
        }

        // Música
        findViewById<LinearLayout>(R.id.btnPlataformaSpotify).setOnClickListener {
            abrirPlataforma("https://open.spotify.com", "com.spotify.music")
        }
        findViewById<LinearLayout>(R.id.btnPlataformaYoutube).setOnClickListener {
            abrirPlataforma("https://music.youtube.com", "com.google.android.apps.youtube.music")
        }
        findViewById<LinearLayout>(R.id.btnPlataformaApple).setOnClickListener {
            abrirPlataforma("https://music.apple.com", "com.apple.android.music")
        }
        findViewById<LinearLayout>(R.id.btnPlataformaAmazon).setOnClickListener {
            abrirPlataforma("https://music.amazon.com", "com.amazon.mp3")
        }
        findViewById<LinearLayout>(R.id.btnPlataformaDeezer).setOnClickListener {
            abrirPlataforma("https://www.deezer.com", "deezer.android.app")
        }
    }

    private fun setupBotones() {
        btnIniciarCronometro.setOnClickListener { startWorkout() }
        btnPausar.setOnClickListener {
            if (isRunning) pauseWorkout() else resumeWorkout()
        }
        btnFinalizar.setOnClickListener { finishWorkout() }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentCarrera) as SupportMapFragment?
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
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }

    private fun startWorkout() {
        isRunning = true
        btnIniciarCronometro.visibility = View.GONE
        layoutControlesActivos.visibility = View.VISIBLE
        handler.post(timerRunnable)
        startLocationUpdates()
    }

    private fun pauseWorkout() {
        isRunning = false
        btnPausar.text = "Reanudar"
        handler.removeCallbacks(timerRunnable)
        stopLocationUpdates()
    }

    private fun resumeWorkout() {
        isRunning = true
        btnPausar.text = "Pausar"
        handler.post(timerRunnable)
        startLocationUpdates()
    }

    private fun finishWorkout() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        stopLocationUpdates()
        finish()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3_000L).setMinUpdateIntervalMillis(2_000L).build()
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
        lastLocation?.let { prev ->
            val result = FloatArray(1)
            Location.distanceBetween(prev.latitude, prev.longitude, location.latitude, location.longitude, result)
            if (result[0] < 50f) {
                totalDistanceMeters += result[0]
                updateStatsUI()
            }
        }
        lastLocation = location
        routePoints.add(newPoint)

        if (routePolyline == null) {
            routePolyline = googleMap?.addPolyline(PolylineOptions().color(Color.RED).width(10f).geodesic(true))
        }
        routePolyline?.points = routePoints
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f))
    }

    private fun updateTimerUI() {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    private fun updateStatsUI() {
        val km = totalDistanceMeters / 1000.0
        tvLiveDistancia.text = String.format("%.2f", km)

        if (elapsedSeconds > 0 && totalDistanceMeters > 0) {
            val paceSecPerKm = elapsedSeconds.toDouble() / km
            val paceMin = (paceSecPerKm / 60).toInt()
            val paceSec = (paceSecPerKm % 60).toInt()
            tvLiveRitmo.text = String.format("%d'%02d\"", paceMin, paceSec)
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
}