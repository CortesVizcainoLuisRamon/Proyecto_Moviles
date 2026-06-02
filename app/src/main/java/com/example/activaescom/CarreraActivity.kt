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
import android.speech.tts.TextToSpeech
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
import java.util.Locale
import com.example.activaescom.database.entities.CarreraDetalleEntity
import com.example.activaescom.viewmodel.CarreraDetalleViewModel
import com.example.activaescom.database.entities.CarreraConfiguracionEntity
import com.example.activaescom.viewmodel.CarreraConfiguracionViewModel
import com.example.activaescom.database.AppDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast

class CarreraActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var carreraDetalleViewModel: CarreraDetalleViewModel

    private lateinit var carreraConfiguracionViewModel: CarreraConfiguracionViewModel
    // Dashboard vivo
    private lateinit var cardDashboardVivo: CardView
    private lateinit var tvFaseEntrenamiento: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvDistancia: TextView
    private lateinit var tvRitmo: TextView
    private lateinit var tvCalorias: TextView

    // Controles
    private lateinit var btnIniciarCronometro: MaterialButton
    private lateinit var layoutControlesActivos: LinearLayout
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton
    private lateinit var tvGpsEstado: TextView

    // Opciones de entrenamiento
    private lateinit var botonesObjetivo: List<Button>
    private lateinit var botonesZona: List<Button>
    private lateinit var botonesSuperficie: List<Button>
    private lateinit var switchCalentamiento: SwitchCompat
    private lateinit var layoutDuracionCalentamiento: LinearLayout
    private lateinit var botonesCalentamiento: List<Button>
    private var minutosCalentamiento: Int = 5

    private var objetivoSeleccionado =
        "Resistencia"

    private var zonaSeleccionada =
        "Z3"

    private var superficieSeleccionada =
        "Asfalto"
    private lateinit var spinnerAvisos: Spinner

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

    // isWarmingUp: true mientras está en fase de calentamiento
    // warmupLimitSeconds: cuántos segundos dura el calentamiento elegido
    // workoutSeconds: solo cuenta el tiempo real de carrera (sin calentamiento)
    private var isWarmingUp = false
    private var warmupLimitSeconds = 0L
    private var workoutSeconds = 0L

    // Estadísticas (solo cuentan en fase de carrera real)
    private var totalDistanceMeters = 0.0

    // Motor de Voz (TextToSpeech)
    private lateinit var textToSpeech: TextToSpeech
    private var isTtsReady = false
    private var lastAnnouncedKm = 0
    private var lastAnnouncedMinute = 0

    companion object {
        private const val REQUEST_LOCATION = 101
    }

    // ─────────────────────────────────────────────
    // CRONÓMETRO
    // ─────────────────────────────────────────────
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds++

                if (isWarmingUp) {
                    if (elapsedSeconds >= warmupLimitSeconds) {
                        transicionACarrera()
                    } else {
                        updateTimerUI()
                    }
                } else {
                    workoutSeconds++
                    updateTimerUI()

                    // Aviso por voz cada 5 minutos
                    val opcionAviso = spinnerAvisos.selectedItem?.toString() ?: "Sin avisos"
                    if (opcionAviso == "Cada 5 min" && workoutSeconds > 0 && workoutSeconds % 300L == 0L) {
                        val minTranscurridos = (workoutSeconds / 60).toInt()
                        if (minTranscurridos > lastAnnouncedMinute) {
                            lastAnnouncedMinute = minTranscurridos
                            val km = totalDistanceMeters / 1000.0
                            hablarMensaje("Llevas $minTranscurridos minutos.")
                        }
                    }
                }

                handler.postDelayed(this, 1000)
            }
        }
    }

    // ─────────────────────────────────────────────
    // CICLO DE VIDA
    // ─────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val entrenamientoId =

            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        if (entrenamientoId == -1) {

            Toast.makeText(
                this,
                "Primero inicia un nuevo entrenamiento",
                Toast.LENGTH_LONG
            ).show()

            startActivity(
                Intent(
                    this,
                    NuevoEntrenamientoActivity::class.java
                )
            )

            finish()

            return
        }

        setContentView(R.layout.activity_carrera)

        val metaKm =
            intent.getDoubleExtra(
                "META_KM",
                0.0
            )
        carreraDetalleViewModel =
            CarreraDetalleViewModel(
                application
            )

        carreraConfiguracionViewModel =
            CarreraConfiguracionViewModel(
                application
            )

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        cargarDatosConfiguracion()
        setupOpcionesEntrenamiento()
        setupBotones()
        initTextToSpeech()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    // ─────────────────────────────────────────────
    // VINCULAR VISTAS
    // ─────────────────────────────────────────────
    private fun bindViews() {
        cardDashboardVivo      = findViewById(R.id.cardDashboardVivo)
        tvFaseEntrenamiento    = findViewById(R.id.tvFaseEntrenamiento)
        tvTimer                = findViewById(R.id.tvTimer)
        tvDistancia            = findViewById(R.id.tvDistancia)
        tvRitmo                = findViewById(R.id.tvRitmo)
        tvCalorias             = findViewById(R.id.tvCalorias)
        btnIniciarCronometro   = findViewById(R.id.btnIniciarCronometro)
        layoutControlesActivos = findViewById(R.id.layoutControlesActivos)
        btnPausar              = findViewById(R.id.btnPausar)
        btnFinalizar           = findViewById(R.id.btnFinalizar)
        tvGpsEstado            = findViewById(R.id.tvGpsEstado)

        botonesObjetivo = listOf(
            findViewById(R.id.btnObjetivoResistencia),
            findViewById(R.id.btnObjetivoVelocidad),
            findViewById(R.id.btnObjetivoRecuperacion),
            findViewById(R.id.btnObjetivoCompetencia)
        )
        botonesZona = listOf(
            findViewById(R.id.btnZona1), findViewById(R.id.btnZona2),
            findViewById(R.id.btnZona3), findViewById(R.id.btnZona4),
            findViewById(R.id.btnZona5)
        )
        botonesSuperficie = listOf(
            findViewById(R.id.btnSuperficieAsfalto),
            findViewById(R.id.btnSuperficieTierra),
            findViewById(R.id.btnSuperficiePista)
        )
        switchCalentamiento         = findViewById(R.id.switchCalentamiento)
        layoutDuracionCalentamiento = findViewById(R.id.layoutDuracionCalentamiento)
        botonesCalentamiento = listOf(
            findViewById(R.id.btnCal5),
            findViewById(R.id.btnCal10),
            findViewById(R.id.btnCal15)
        )
        spinnerAvisos = findViewById(R.id.spinnerAvisos)
    }

    // ─────────────────────────────────────────────
    // DATOS DE CONFIGURACIÓN
    // ─────────────────────────────────────────────
    private fun cargarDatosConfiguracion() {
        findViewById<TextView>(R.id.tvConfigRutinaTitulo).text =
            intent.getStringExtra("NOMBRE_RUTINA") ?: "Carrera Individual"
        findViewById<TextView>(R.id.tvConfigMeta).text =
            "Meta: ${intent.getStringExtra("META_DISTANCIA") ?: "5.0 km"}"
        findViewById<TextView>(R.id.tvConfigSueno).text =
            "💤 : ${intent.getIntExtra("HORAS_SUENO", 7)} hrs"
        findViewById<TextView>(R.id.tvConfigLugar).text =
            "📍 : ${intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Pista"}"
        findViewById<TextView>(R.id.tvConfigDuracion).text =
            "⏱️ : ${intent.getIntExtra("DURACION_ESTIMADA", 30)} min"
        findViewById<TextView>(R.id.guardarNotas).text =
            intent.getStringExtra("FECHA_ENTRENAMIENTO") ?: "No especificada"

        val notasPrevias = intent.getStringExtra("NOTAS") ?: ""
        val layoutNotas = findViewById<LinearLayout>(R.id.layoutConfigNotas)
        if (notasPrevias.isNotEmpty()) {
            findViewById<TextView>(R.id.tvConfigNotas).text = "Notas previas: $notasPrevias"
            layoutNotas.visibility = View.VISIBLE
        } else {
            layoutNotas.visibility = View.GONE
        }

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

    // ─────────────────────────────────────────────
    // OPCIONES DE ENTRENAMIENTO
    // ─────────────────────────────────────────────
    private fun setupOpcionesEntrenamiento() {
        for (btn in botonesObjetivo)   { btn.setOnClickListener { seleccionarChip(btn, botonesObjetivo) } }
        for (btn in botonesZona)       { btn.setOnClickListener { seleccionarChip(btn, botonesZona) } }
        for (btn in botonesSuperficie) { btn.setOnClickListener { seleccionarChip(btn, botonesSuperficie) } }

        switchCalentamiento.setOnCheckedChangeListener { _, isChecked ->
            layoutDuracionCalentamiento.visibility =
                if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) minutosCalentamiento = 5
        }

        val duraciones = mapOf(
            R.id.btnCal5  to 5,
            R.id.btnCal10 to 10,
            R.id.btnCal15 to 15
        )
        for (btn in botonesCalentamiento) {
            btn.setOnClickListener {
                minutosCalentamiento = duraciones[btn.id] ?: 5
                seleccionarChip(btn, botonesCalentamiento)
            }
        }

        val opcionesAvisos = listOf("Cada 1 km", "Cada 5 min", "Solo inicio/fin", "Sin avisos")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesAvisos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAvisos.adapter = adapter
    }

    private fun seleccionarChip(
        seleccionado: Button,
        grupo: List<Button>
    ) {

        for (btn in grupo) {

            if (btn == seleccionado) {

                btn.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this,
                        R.color.red_1
                    )

                btn.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.white
                    )
                )

            } else {

                btn.backgroundTintList =
                    ContextCompat.getColorStateList(
                        this,
                        R.color.gray_light
                    )

                btn.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.gray_text
                    )
                )
            }
        }

        // =================================
        // GUARDAR SELECCIÓN
        // =================================

        when (grupo) {

            botonesObjetivo -> {

                objetivoSeleccionado =
                    seleccionado.text.toString()
            }

            botonesZona -> {

                zonaSeleccionada =
                    seleccionado.text.toString()
            }

            botonesSuperficie -> {

                superficieSeleccionada =
                    seleccionado.text.toString()
            }
        }
    }

    // ─────────────────────────────────────────────
    // MOTOR DE VOZ (TEXT TO SPEECH)
    // ─────────────────────────────────────────────
    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale("es", "MX"))
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isTtsReady = true
                }
            }
        }
    }

    private fun hablarMensaje(mensaje: String) {
        if (isTtsReady) {
            textToSpeech.speak(mensaje, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // ─────────────────────────────────────────────
    // BOTONES DEL CRONÓMETRO
    // ─────────────────────────────────────────────
    private fun setupBotones() {
        btnIniciarCronometro.setOnClickListener { startWorkout() }
        btnPausar.setOnClickListener {
            if (isRunning) pauseWorkout() else resumeWorkout()
        }
        btnFinalizar.setOnClickListener {
            android.widget.Toast.makeText(this, "¡Entrenamiento finalizado con éxito!", android.widget.Toast.LENGTH_SHORT).show()
            finishWorkout()
        }
    }

    // ─────────────────────────────────────────────
    // CONTROL DEL ENTRENAMIENTO
    // ─────────────────────────────────────────────
    private fun startWorkout() {
        val entrenamientoId =

            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        val configuracion =
            CarreraConfiguracionEntity(

                entrenamientoId =
                    entrenamientoId,

                objetivo =
                    objetivoSeleccionado,

                zonaCardiaca =
                    zonaSeleccionada,

                superficie =
                    superficieSeleccionada,

                calentamientoActivo =
                    switchCalentamiento.isChecked,

                minutosCalentamiento =
                    minutosCalentamiento,

                avisosVoz =
                    spinnerAvisos
                        .selectedItem
                        .toString()
            )

        carreraConfiguracionViewModel
            .insertarConfiguracionCarrera(
                configuracion
            )
        isRunning = true
        btnIniciarCronometro.visibility   = View.GONE
        cardDashboardVivo.visibility      = View.VISIBLE
        layoutControlesActivos.visibility = View.VISIBLE

        val opcionAviso = spinnerAvisos.selectedItem?.toString() ?: "Sin avisos"

        if (switchCalentamiento.isChecked) {
            isWarmingUp         = true
            warmupLimitSeconds  = minutosCalentamiento * 60L
            elapsedSeconds      = 0L
            workoutSeconds      = 0L
            totalDistanceMeters = 0.0
            lastAnnouncedKm     = 0
            lastAnnouncedMinute = 0

            tvFaseEntrenamiento.text = "🔥 CALENTAMIENTO"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            tvTimer.setTextColor(Color.parseColor("#FF8C00"))

            if (opcionAviso == "Solo inicio/fin") {
                hablarMensaje("Iniciando calentamiento previo.")
            }
        } else {
            isWarmingUp         = false
            elapsedSeconds      = 0L
            workoutSeconds      = 0L
            totalDistanceMeters = 0.0
            lastAnnouncedKm     = 0
            lastAnnouncedMinute = 0

            tvFaseEntrenamiento.text = "🏃 EN CARRERA"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
            tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))

            if (opcionAviso == "Solo inicio/fin") {
                hablarMensaje("Iniciando carrera.")
            }
        }

        handler.post(timerRunnable)
        startLocationUpdates()
    }

    private fun transicionACarrera() {
        isWarmingUp    = false
        workoutSeconds = 0L

        try {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val pattern = VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1)
                vibrator.vibrate(pattern)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 300, 150, 300), -1)
            }
        } catch (e: Exception) { }

        tvFaseEntrenamiento.text = "🏃 EN CARRERA"
        tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))

        totalDistanceMeters = 0.0
        lastAnnouncedKm     = 0
        lastAnnouncedMinute = 0
        tvDistancia.text    = "0.0"
        tvRitmo.text        = "--:--"
        tvCalorias.text     = "0"

        updateTimerUI()

        val opcionAviso = spinnerAvisos.selectedItem?.toString() ?: "Sin avisos"
        if (opcionAviso == "Solo inicio/fin") {
            hablarMensaje("Calentamiento terminado. Iniciando carrera.")
        }
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

    private fun finishWorkout() {

        isRunning = false

        handler.removeCallbacks(timerRunnable)

        stopLocationUpdates()

        // GUARDAR MÉTRICAS

        val entrenamientoId =
            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        val calorias =
            tvCalorias.text
                .toString()
                .replace(Regex("[^0-9]"), "")
                .toIntOrNull() ?: 0

        lifecycleScope.launch {

            AppDatabase
                .getDatabase(this@CarreraActivity)
                .entrenamientoDao()
                .actualizarResultados(
                    entrenamientoId,
                    workoutSeconds,
                    calorias
                )
        }

        val km =
            totalDistanceMeters / 1000.0

        val velocidadPromedio =

            if (workoutSeconds > 0) {

                km / (workoutSeconds / 3600.0)

            } else {

                0.0
            }

        val detalle = CarreraDetalleEntity(

            entrenamientoId =
                entrenamientoId,

            distanciaKm =
                km,

            caloriasQuemadas =
                tvCalorias.text
                    .toString()
                    .toInt(),

            duracionSegundos =
                workoutSeconds,

            ritmoPromedio =
                tvRitmo.text.toString(),

            velocidadPromedio =
                velocidadPromedio,

            pasos =
                (km * 1300).toInt()
        )

        carreraDetalleViewModel
            .insertarDetalleCarrera(
                detalle
            )

        // =====================================
        // AVISOS DE VOZ
        // =====================================

        val opcionAviso =
            spinnerAvisos
                .selectedItem
                ?.toString()
                ?: "Sin avisos"

        if (opcionAviso == "Solo inicio/fin") {

            hablarMensaje(
                "Entrenamiento finalizado."
            )

            handler.postDelayed({

                navegarAMain()

            }, 3000)

        } else {

            navegarAMain()
        }
    }

    // ─────────────────────────────────────────────
    // MAPA
    // ─────────────────────────────────────────────
    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragmentCarrera) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isCompassEnabled      = true
        }
        showInitialLocation()
    }

    private fun showInitialLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            tvGpsEstado.text = "📍 Obteniendo ubicación..."
            googleMap?.isMyLocationEnabled = true
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        tvGpsEstado.text = "📍 GPS activo"
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16f))
                    } else {
                        tvGpsEstado.text = "📍 Buscando GPS..."
                    }
                }
                .addOnFailureListener {
                    tvGpsEstado.text = "📍 Buscando GPS..."
                }
        } else {
            tvGpsEstado.text = "📍 Buscando GPS..."
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }

    // ─────────────────────────────────────────────
    // UBICACIÓN EN TIEMPO REAL
    // ─────────────────────────────────────────────
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3_000L)
            .setMinUpdateIntervalMillis(2_000L).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { processNewLocation(it) }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            request, locationCallback!!, Looper.getMainLooper()
        )
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
                Location.distanceBetween(
                    prev.latitude, prev.longitude,
                    location.latitude, location.longitude,
                    result
                )
                if (result[0] < 50f) {
                    totalDistanceMeters += result[0]
                    updateStatsUI()
                }
            }
        }
        lastLocation = location
        routePoints.add(newPoint)

        if (routePolyline == null) {
            routePolyline = googleMap?.addPolyline(
                PolylineOptions().color(Color.RED).width(10f).geodesic(true)
            )
        }
        routePolyline?.points = routePoints
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 17f))
    }

    // ─────────────────────────────────────────────
    // ACTUALIZAR UI Y AVISOS POR VOZ
    // ─────────────────────────────────────────────
    private fun updateTimerUI() {
        val segundos = if (isWarmingUp) elapsedSeconds else workoutSeconds
        tvTimer.text = String.format("%02d:%02d:%02d", segundos / 3600, (segundos % 3600) / 60, segundos % 60)
    }

    private fun updateStatsUI() {
        val km = totalDistanceMeters / 1000.0
        tvDistancia.text = String.format("%.3f", km)

        if (workoutSeconds > 0 && totalDistanceMeters > 0) {
            val paceSecPerKm = workoutSeconds.toDouble() / km
            val paceMin = (paceSecPerKm / 60).toInt()
            val paceSec = (paceSecPerKm % 60).toInt()
            tvRitmo.text = String.format("%d'%02d\"", paceMin, paceSec)

            tvCalorias.text = (km * 70).toInt().toString()

            // ---- LÓGICA DE AVISOS CADA 1 KM ----
            val kmEntero = km.toInt()
            if (kmEntero > 0 && kmEntero > lastAnnouncedKm) {
                val opcionAviso = spinnerAvisos.selectedItem?.toString() ?: "Sin avisos"
                if (opcionAviso == "Cada 1 km") {
                    lastAnnouncedKm = kmEntero
                    hablarMensaje("Has recorrido $kmEntero kilómetros.")
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // MÚSICA
    // ─────────────────────────────────────────────
    private fun abrirPlataforma(urlWeb: String, packageName: String) {
        val intentApp = packageManager.getLaunchIntentForPackage(packageName)
        if (intentApp != null) startActivity(intentApp)
        else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb)))
    }

    // ─────────────────────────────────────────────
    // CICLO DE VIDA Y PERMISOS
    // ─────────────────────────────────────────────
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            showInitialLocation()
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
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
        val intent = android.content.Intent(this, MainActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}