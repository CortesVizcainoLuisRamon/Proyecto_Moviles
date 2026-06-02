package com.example.activaescom

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
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
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.activaescom.database.entities.FuerzaConfiguracionEntity
import com.example.activaescom.database.entities.FuerzaDetalleEntity
import com.example.activaescom.viewmodel.FuerzaViewModel
import com.example.activaescom.database.AppDatabase
import kotlinx.coroutines.launch

class FuerzaActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fuerzaViewModel: FuerzaViewModel

    // Mapa y Ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private lateinit var tvGpsEstado: TextView
    private var initialLocationCallback: LocationCallback? = null

    // UI Calentamiento
    private lateinit var switchCalentamiento: SwitchCompat
    private lateinit var layoutDuracionCalentamiento: LinearLayout
    private lateinit var botonesCalentamiento: List<Button>

    // UI Grupos Musculares
    private lateinit var btnPecho: Button
    private lateinit var btnEspalda: Button
    private lateinit var btnPiernas: Button
    private lateinit var btnBrazos: Button
    private lateinit var btnHombros: Button

    // UI Ejercicio Actual (Registro)
    private lateinit var etSeries: EditText
    private lateinit var etReps: EditText
    private lateinit var etPeso: EditText
    private lateinit var btnRegistrarSerie: Button

    // Dashboard Vivo
    private lateinit var cardDashboardVivo: CardView
    private lateinit var tvFaseEntrenamiento: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvSeriesDash: TextView
    private lateinit var tvRepsDash: TextView
    private lateinit var tvVolumenDash: TextView

    // Controles
    private lateinit var btnIniciarCronometro: MaterialButton
    private lateinit var layoutControlesActivos: LinearLayout
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    // Estado del Entrenamiento y Cronómetro
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var isWarmingUp = false
    private var minutosCalentamiento = 5
    private var warmupLimitSeconds = 0L
    private var workoutSeconds = 0L
    private var elapsedSeconds = 0L

    // Datos de la Simulación Inteligente
    private var selectedMuscleGroup = "Pecho"
    private var hasTarget = false
    private var targetSeries = 0
    private var targetReps = 0
    private var targetPeso = 0.0

    companion object {
        private const val REQUEST_LOCATION = 101
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds++
                if (isWarmingUp) {
                    if (elapsedSeconds >= warmupLimitSeconds) {
                        transicionRutinaFuerza()
                    } else {
                        updateTimerUI()
                    }
                } else {
                    workoutSeconds++
                    updateTimerUI()
                    if (hasTarget) {
                        simularEsfuerzoFuerza()
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

        setContentView(R.layout.activity_fuerza)

        fuerzaViewModel =
            FuerzaViewModel(
                application
            )
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        cargarDatosConfiguracion()
        setupOpcionesEntrenamiento()
        setupMuscleGroupButtons()
        setupControlButtons()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    // ─────────────────────────────────────────────
    // VINCULAR VISTAS
    // ─────────────────────────────────────────────
    private fun bindViews() {
        tvGpsEstado = findViewById(R.id.tvGpsEstado)

        switchCalentamiento         = findViewById(R.id.switchCalentamiento)
        layoutDuracionCalentamiento = findViewById(R.id.layoutDuracionCalentamiento)
        botonesCalentamiento = listOf(
            findViewById(R.id.btnCal5),
            findViewById(R.id.btnCal10),
            findViewById(R.id.btnCal15)
        )

        btnPecho    = findViewById(R.id.btnPecho)
        btnEspalda  = findViewById(R.id.btnEspalda)
        btnPiernas  = findViewById(R.id.btnPiernas)
        btnBrazos   = findViewById(R.id.btnBrazos)
        btnHombros  = findViewById(R.id.btnHombros)

        etSeries         = findViewById(R.id.etSeries)
        etReps           = findViewById(R.id.etReps)
        etPeso           = findViewById(R.id.etPeso)
        btnRegistrarSerie = findViewById(R.id.btnRegistrarSerie)

        cardDashboardVivo      = findViewById(R.id.cardDashboardVivo)
        tvFaseEntrenamiento    = findViewById(R.id.tvFaseEntrenamiento)
        tvTimer                = findViewById(R.id.tvTimer)
        tvSeriesDash           = findViewById(R.id.tvSeriesDash)
        tvRepsDash             = findViewById(R.id.tvRepsDash)
        tvVolumenDash          = findViewById(R.id.tvVolumenDash)

        btnIniciarCronometro   = findViewById(R.id.btnIniciarCronometro)
        layoutControlesActivos = findViewById(R.id.layoutControlesActivos)
        btnPausar              = findViewById(R.id.btnPausar)
        btnFinalizar           = findViewById(R.id.btnFinalizar)
    }

    // ─────────────────────────────────────────────
    // DATOS DE CONFIGURACIÓN
    // ─────────────────────────────────────────────
    private fun cargarDatosConfiguracion() {
        val nombreRutina  = intent.getStringExtra("NOMBRE_RUTINA") ?: "Rutina de Fuerza"
        val enfoqueFuerza = intent.getStringExtra("ENFOQUE_FUERZA") ?: "Hipertrofia"

        findViewById<TextView>(R.id.tvConfigRutinaTitulo).text = nombreRutina
        findViewById<TextView>(R.id.tvConfigMeta).text         = "Enfoque: $enfoqueFuerza"
        findViewById<TextView>(R.id.tvConfigSueno).text        = "💤 : ${intent.getIntExtra("HORAS_SUENO", 7)} hrs"
        findViewById<TextView>(R.id.tvConfigLugar).text        = "📍 : ${intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Gimnasio"}"
        findViewById<TextView>(R.id.tvConfigDuracion).text     = "⏱️ : ${intent.getIntExtra("DURACION_ESTIMADA", 60)} min"
        findViewById<TextView>(R.id.guardarNotas).text         = intent.getStringExtra("FECHA_ENTRENAMIENTO") ?: "No especificada"

        val notasPrevias = intent.getStringExtra("NOTAS") ?: ""
        val layoutNotas  = findViewById<LinearLayout>(R.id.layoutConfigNotas)
        if (notasPrevias.isNotEmpty()) {
            findViewById<TextView>(R.id.tvConfigNotas).text = "Notas previas: $notasPrevias"
            layoutNotas.visibility = View.VISIBLE
        } else {
            layoutNotas.visibility = View.GONE
        }

        findViewById<LinearLayout>(R.id.btnPlataformaSpotify).setOnClickListener { abrirPlataforma("https://open.spotify.com", "com.spotify.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaYoutube).setOnClickListener { abrirPlataforma("https://music.youtube.com", "com.google.android.apps.youtube.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaApple).setOnClickListener { abrirPlataforma("https://music.apple.com", "com.apple.android.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaAmazon).setOnClickListener { abrirPlataforma("https://music.amazon.com", "com.amazon.mp3") }
        findViewById<LinearLayout>(R.id.btnPlataformaDeezer).setOnClickListener { abrirPlataforma("https://www.deezer.com", "deezer.android.app") }
    }

    private fun setupOpcionesEntrenamiento() {
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
    }

    private fun setupMuscleGroupButtons() {
        val gruposMusculares = listOf(btnPecho, btnEspalda, btnPiernas, btnBrazos, btnHombros)
        val nombres = mapOf(
            btnPecho   to "Pecho",
            btnEspalda to "Espalda",
            btnPiernas to "Piernas",
            btnBrazos  to "Brazos",
            btnHombros to "Hombros"
        )
        for (btn in gruposMusculares) {
            btn.setOnClickListener {
                selectedMuscleGroup = nombres[btn] ?: "Pecho"
                seleccionarChip(btn, gruposMusculares)
            }
        }
    }

    private fun seleccionarChip(seleccionado: Button, grupo: List<Button>) {
        for (btn in grupo) {
            if (btn == seleccionado) {
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_1))
                btn.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                btn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray_light))
                btn.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            }
        }
    }

    // ─────────────────────────────────────────────
    // BOTONES DEL CRONÓMETRO
    // ─────────────────────────────────────────────
    private fun setupControlButtons() {
        btnIniciarCronometro.setOnClickListener { startWorkout() }
        btnPausar.setOnClickListener { if (isRunning) pauseWorkout() else resumeWorkout() }
        btnFinalizar.setOnClickListener {
            android.widget.Toast.makeText(this, "¡Entrenamiento finalizado con éxito!", android.widget.Toast.LENGTH_SHORT).show()
            stopWorkout()
            navegarAMain()
        }

        btnRegistrarSerie.setOnClickListener {
            guardarMetaEjercicio()
        }
    }

    // ─────────────────────────────────────────────
    // LÓGICA DE SIMULACIÓN Y METAS
    // ─────────────────────────────────────────────
    private fun guardarMetaEjercicio() {
        val seriesText = etSeries.text.toString()
        val repsText = etReps.text.toString()
        val pesoText = etPeso.text.toString()

        if (seriesText.isNotEmpty() && repsText.isNotEmpty() && pesoText.isNotEmpty()) {
            targetSeries = seriesText.toInt()
            targetReps = repsText.toInt()
            targetPeso = pesoText.toDouble()
            hasTarget = true

            // Ocultar teclado
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            Toast.makeText(this, "Meta guardada. Inicia el cronómetro para simular.", Toast.LENGTH_SHORT).show()
            btnRegistrarSerie.text = "✓ Meta Lista"
            btnRegistrarSerie.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray_text))
        } else {
            Toast.makeText(this, "Ingresa las series, repeticiones y peso", Toast.LENGTH_SHORT).show()
        }
    }

    private fun simularEsfuerzoFuerza() {
        // Tiempos promedio según el grupo muscular
        val (secPerRep, restSec) = when (selectedMuscleGroup) {
            "Piernas" -> Pair(4, 90) // Movimiento pesado y descanso largo
            "Pecho", "Espalda" -> Pair(3, 60) // Movimiento moderado
            else -> Pair(2, 45) // Brazos y Hombros, más rápido
        }

        val timeForOneSet = targetReps * secPerRep
        val timeForSetAndRest = timeForOneSet + restSec

        // Calcular progreso basado en los segundos transcurridos
        var completedSets = (workoutSeconds / timeForSetAndRest).toInt()
        val currentSetSeconds = (workoutSeconds % timeForSetAndRest).toInt()

        // Límite: no pasarse de la meta
        if (completedSets >= targetSeries) {
            completedSets = targetSeries
            updateDashboardStatsUI(completedSets, completedSets * targetReps, (completedSets * targetReps) * targetPeso)
            return
        }

        var repsInCurrentSet = 0
        if (currentSetSeconds < timeForOneSet) {
            // El usuario está activamente levantando
            repsInCurrentSet = currentSetSeconds / secPerRep
            tvFaseEntrenamiento.text = "💪 LEVANTANDO ($selectedMuscleGroup)"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        } else {
            // El usuario está en su periodo de descanso
            repsInCurrentSet = targetReps
            tvFaseEntrenamiento.text = "⏱️ DESCANSO"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
        }

        val totalRepsDone = (completedSets * targetReps) + repsInCurrentSet
        val totalVolumeDone = totalRepsDone * targetPeso

        updateDashboardStatsUI(completedSets, totalRepsDone, totalVolumeDone)
    }

    private fun updateDashboardStatsUI(series: Int, reps: Int, volume: Double) {
        tvSeriesDash.text  = series.toString()
        tvRepsDash.text    = reps.toString()
        tvVolumenDash.text = String.format("%.1f", volume)
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

            FuerzaConfiguracionEntity(

                entrenamientoId =
                    entrenamientoId,

                calentamientoActivo =
                    switchCalentamiento
                        .isChecked,

                minutosCalentamiento =
                    minutosCalentamiento,

                grupoMuscular =
                    selectedMuscleGroup
            )

        lifecycleScope.launch {

            fuerzaViewModel
                .insertarConfiguracion(
                    configuracion
                )
        }
        isRunning = true
        btnIniciarCronometro.visibility   = View.GONE
        cardDashboardVivo.visibility      = View.VISIBLE
        layoutControlesActivos.visibility = View.VISIBLE

        updateDashboardStatsUI(0, 0, 0.0)

        if (switchCalentamiento.isChecked) {
            isWarmingUp        = true
            warmupLimitSeconds = minutosCalentamiento * 60L
            elapsedSeconds     = 0L
            workoutSeconds     = 0L
            tvFaseEntrenamiento.text = "🔥 CALENTAMIENTO"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            tvTimer.setTextColor(Color.parseColor("#FF8C00"))
        } else {
            isWarmingUp    = false
            elapsedSeconds = 0L
            workoutSeconds = 0L
            tvFaseEntrenamiento.text = "💪 PREPARANDO..."
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
            tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        }

        handler.post(timerRunnable)
    }

    private fun transicionRutinaFuerza() {
        isWarmingUp    = false
        workoutSeconds = 0L

        try {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 300), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 300, 150, 300), -1)
            }
        } catch (e: Exception) { }

        tvFaseEntrenamiento.text = "💪 A LEVANTAR"
        tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))

        updateDashboardStatsUI(0, 0, 0.0)
        updateTimerUI()
    }

    private fun pauseWorkout() {
        isRunning = false
        btnPausar.setIconResource(R.drawable.play)
        handler.removeCallbacks(timerRunnable)
    }

    private fun resumeWorkout() {
        isRunning = true
        btnPausar.setIconResource(R.drawable.pause)
        handler.post(timerRunnable)
    }

    private fun stopWorkout() {

        val entrenamientoId =

            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        val volumenTotal =

            tvVolumenDash
                .text
                .toString()
                .toDoubleOrNull()
                ?: 0.0

        val detalle =

            FuerzaDetalleEntity(

                entrenamientoId =
                    entrenamientoId,

                seriesObjetivo =
                    targetSeries,

                repeticionesObjetivo =
                    targetReps,

                pesoObjetivo =
                    targetPeso,

                volumenTotal =
                    volumenTotal,

                duracionSegundos =
                    workoutSeconds
            )

        lifecycleScope.launch {

            fuerzaViewModel
                .insertarDetalle(
                    detalle
                )
        }
        val calorias = (
                volumenTotal / 10
                ).toInt()

        lifecycleScope.launch {

            AppDatabase
                .getDatabase(this@FuerzaActivity)
                .entrenamientoDao()
                .actualizarResultados(
                    entrenamientoId,
                    workoutSeconds,
                    calorias
                )
        }
        isRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimerUI() {
        val segundos = if (isWarmingUp) elapsedSeconds else workoutSeconds
        val h = segundos / 3600
        val m = (segundos % 3600) / 60
        val s = segundos % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    // ─────────────────────────────────────────────
    // MAPA
    // ─────────────────────────────────────────────
    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentFuerza) as SupportMapFragment?
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
            tvGpsEstado.text = "📍 Buscando GPS..."
            googleMap?.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    tvGpsEstado.text = "📍 GPS activo"
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 16f))
                } else {
                    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L).setMaxUpdates(1).build()
                    initialLocationCallback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            result.lastLocation?.let {
                                tvGpsEstado.text = "📍 GPS activo"
                                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 16f))
                            }
                            initialLocationCallback?.let { cb -> fusedLocationClient.removeLocationUpdates(cb) }
                            initialLocationCallback = null
                        }
                    }
                    fusedLocationClient.requestLocationUpdates(request, initialLocationCallback!!, Looper.getMainLooper())
                }
            }
        } else {
            tvGpsEstado.text = "📍 Buscando GPS..."
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }

    private fun abrirPlataforma(urlWeb: String, packageName: String) {
        val intentApp = packageManager.getLaunchIntentForPackage(packageName)
        if (intentApp != null) startActivity(intentApp)
        else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb)))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showInitialLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopWorkout()
        initialLocationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
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