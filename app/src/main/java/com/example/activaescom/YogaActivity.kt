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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import kotlin.random.Random
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.activaescom.database.entities.YogaConfiguracionEntity
import com.example.activaescom.database.entities.YogaDetalleEntity
import com.example.activaescom.viewmodel.YogaViewModel

class YogaActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var yogaViewModel: YogaViewModel

    // Mapa y Ubicación
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private lateinit var tvGpsEstado: TextView

    // Opciones de Configuración Yoga
    private lateinit var botonesEstilo: List<Button>
    private lateinit var botonesNivel: List<Button>
    private lateinit var switchSavasana: SwitchCompat
    private lateinit var layoutDuracionSavasana: LinearLayout
    private lateinit var botonesSavasana: List<Button>
    private var minutosSavasana = 3
    private var isInSavasana = false

    // Dashboard vivo
    private lateinit var cardDashboardVivo: CardView
    private lateinit var tvFaseEntrenamiento: TextView
    private lateinit var tvTimer: TextView
    private lateinit var tvPulsoDash: TextView
    private lateinit var tvCaloriasDash: TextView
    private lateinit var tvCiclosDash: TextView

    // Controles de rutina
    private lateinit var btnIniciarCronometro: MaterialButton
    private lateinit var layoutControlesActivos: LinearLayout
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    // Guía de respiración
    private lateinit var btnActivarGuia: Button
    private var breathingGuideActive = false
    private var breathingPhase = 0
    private var breathingSecondsLeft = 4
    private var ciclosCompletados = 0

    // Cronómetro y Simulación de Parámetros
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private var isRunning = false

    private var selectedStyle = "Hatha"
    private var selectedLevel = "Principiante"

    private var baseHeartRate = 90
    private var kcalPerSecond = 0.06
    private var secondsPerBreath = 8
    private var estimatedTotalCalories = 0.0

    companion object {
        private const val REQUEST_LOCATION = 101
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds++

                // Sumar calorías
                estimatedTotalCalories += kcalPerSecond

                updateTimerUI()
                updateSimulatedDash(elapsedSeconds)

                handler.postDelayed(this, 1000)
            }
        }
    }

    private val breathingRunnable = object : Runnable {
        override fun run() {
            if (breathingGuideActive) {
                breathingSecondsLeft--

                if (breathingSecondsLeft <= 0) {
                    val viejaFase = breathingPhase
                    breathingPhase = (breathingPhase + 1) % 3
                    breathingSecondsLeft = when (breathingPhase) {
                        0 -> 4   // Inhala 4s
                        1 -> 7   // Retén 7s
                        else -> 8 // Exhala 8s
                    }

                    if (viejaFase == 2 && breathingPhase == 0) {
                        ciclosCompletados++
                        tvCiclosDash.text = ciclosCompletados.toString()
                    }
                }

                updateBreathingUI()
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_yoga)

        yogaViewModel =
            YogaViewModel(
                application
            )
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        cargarDatosConfiguracion()
        setupOpcionesYoga()
        setupBotones()

        // Calcular los parámetros por defecto
        actualizarParametrosSimulacion()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    private fun bindViews() {
        tvGpsEstado = findViewById(R.id.tvGpsEstado)

        // Configuración
        botonesEstilo = listOf(
            findViewById(R.id.btnEstiloVinyasa), findViewById(R.id.btnEstiloHatha),
            findViewById(R.id.btnEstiloYin), findViewById(R.id.btnEstiloAshtanga)
        )
        botonesNivel = listOf(
            findViewById(R.id.btnNivelPrincipiante), findViewById(R.id.btnNivelIntermedio),
            findViewById(R.id.btnNivelAvanzado)
        )
        switchSavasana = findViewById(R.id.switchSavasana)
        layoutDuracionSavasana = findViewById(R.id.layoutDuracionSavasana)
        botonesSavasana = listOf(
            findViewById(R.id.btnSavasana3), findViewById(R.id.btnSavasana5), findViewById(R.id.btnSavasana10)
        )

        // Dashboard
        cardDashboardVivo = findViewById(R.id.cardDashboardVivo)
        tvFaseEntrenamiento = findViewById(R.id.tvFaseEntrenamiento)
        tvTimer = findViewById(R.id.tvTimer)
        tvPulsoDash = findViewById(R.id.tvPulsoDash)
        tvCaloriasDash = findViewById(R.id.tvCaloriasDash)
        tvCiclosDash = findViewById(R.id.tvCiclosDash)

        // Controles
        btnIniciarCronometro = findViewById(R.id.btnIniciarCronometro)
        layoutControlesActivos = findViewById(R.id.layoutControlesActivos)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        btnActivarGuia = findViewById(R.id.btnActivarGuia)
    }

    private fun setupOpcionesYoga() {
        // Estilos
        findViewById<Button>(R.id.btnEstiloVinyasa).setOnClickListener { selectedStyle = "Vinyasa"; seleccionarChip(it as Button, botonesEstilo); actualizarParametrosSimulacion() }
        findViewById<Button>(R.id.btnEstiloHatha).setOnClickListener { selectedStyle = "Hatha"; seleccionarChip(it as Button, botonesEstilo); actualizarParametrosSimulacion() }
        findViewById<Button>(R.id.btnEstiloYin).setOnClickListener { selectedStyle = "Yin"; seleccionarChip(it as Button, botonesEstilo); actualizarParametrosSimulacion() }
        findViewById<Button>(R.id.btnEstiloAshtanga).setOnClickListener { selectedStyle = "Ashtanga"; seleccionarChip(it as Button, botonesEstilo); actualizarParametrosSimulacion() }

        // Niveles
        findViewById<Button>(R.id.btnNivelPrincipiante).setOnClickListener { selectedLevel = "Principiante"; seleccionarChip(it as Button, botonesNivel); actualizarParametrosSimulacion() }
        findViewById<Button>(R.id.btnNivelIntermedio).setOnClickListener { selectedLevel = "Intermedio"; seleccionarChip(it as Button, botonesNivel); actualizarParametrosSimulacion() }
        findViewById<Button>(R.id.btnNivelAvanzado).setOnClickListener { selectedLevel = "Avanzado"; seleccionarChip(it as Button, botonesNivel); actualizarParametrosSimulacion() }

        switchSavasana.setOnCheckedChangeListener { _, isChecked ->
            layoutDuracionSavasana.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) minutosSavasana = 3
        }

        val duraciones = mapOf(R.id.btnSavasana3 to 3, R.id.btnSavasana5 to 5, R.id.btnSavasana10 to 10)
        for (btn in botonesSavasana) {
            btn.setOnClickListener {
                minutosSavasana = duraciones[btn.id] ?: 3
                seleccionarChip(btn, botonesSavasana)
            }
        }
    }

    private fun actualizarParametrosSimulacion() {
        val multiplicadorNivel = when (selectedLevel) {
            "Principiante" -> 1.0
            "Intermedio" -> 1.1
            else -> 1.25 // Avanzado
        }

        when (selectedStyle) {
            "Vinyasa" -> {
                baseHeartRate = 115
                kcalPerSecond = 0.10 * multiplicadorNivel
                secondsPerBreath = 6
            }
            "Ashtanga" -> {
                baseHeartRate = 125
                kcalPerSecond = 0.12 * multiplicadorNivel
                secondsPerBreath = 5
            }
            "Yin" -> {
                baseHeartRate = 75
                kcalPerSecond = 0.04 * multiplicadorNivel
                secondsPerBreath = 12
            }
            else -> { // Hatha (Default)
                baseHeartRate = 90
                kcalPerSecond = 0.06 * multiplicadorNivel
                secondsPerBreath = 8
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

    private fun cargarDatosConfiguracion() {
        val nombreRutina = intent.getStringExtra("NOMBRE_RUTINA") ?: "Sesión de Yoga"
        val enfoqueYoga = intent.getStringExtra("ENFOQUE_YOGA") ?: "Flexibilidad"

        findViewById<TextView>(R.id.tvConfigRutinaTitulo).text = nombreRutina
        findViewById<TextView>(R.id.tvConfigMeta).text = "Enfoque: $enfoqueYoga"
        findViewById<TextView>(R.id.tvConfigSueno).text = "💤 : ${intent.getIntExtra("HORAS_SUENO", 7)} hrs"
        findViewById<TextView>(R.id.tvConfigLugar).text = "📍 : ${intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Casa"}"
        findViewById<TextView>(R.id.tvConfigDuracion).text = "⏱️ : ${intent.getIntExtra("DURACION_ESTIMADA", 30)} min"
        findViewById<TextView>(R.id.guardarNotas).text = intent.getStringExtra("FECHA_ENTRENAMIENTO") ?: "No especificada"

        val notasPrevias = intent.getStringExtra("NOTAS") ?: ""
        val layoutNotas = findViewById<LinearLayout>(R.id.layoutConfigNotas)
        if (notasPrevias.isNotEmpty()) {
            findViewById<TextView>(R.id.tvConfigNotas).text = "Notas previas: $notasPrevias"
            layoutNotas.visibility = View.VISIBLE
        } else {
            layoutNotas.visibility = View.GONE
        }

        // Música
        findViewById<LinearLayout>(R.id.btnPlataformaSpotify).setOnClickListener { abrirPlataforma("https://open.spotify.com", "com.spotify.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaYoutube).setOnClickListener { abrirPlataforma("https://music.youtube.com", "com.google.android.apps.youtube.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaApple).setOnClickListener { abrirPlataforma("https://music.apple.com", "com.apple.android.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaAmazon).setOnClickListener { abrirPlataforma("https://music.amazon.com", "com.amazon.mp3") }
        findViewById<LinearLayout>(R.id.btnPlataformaDeezer).setOnClickListener { abrirPlataforma("https://www.deezer.com", "deezer.android.app") }
    }

    private fun setupBotones() {
        btnIniciarCronometro.setOnClickListener { startWorkout() }
        btnPausar.setOnClickListener { if (isRunning) pauseWorkout() else resumeWorkout() }
        btnFinalizar.setOnClickListener {
            android.widget.Toast.makeText(this, "¡Entrenamiento finalizado con éxito!", android.widget.Toast.LENGTH_SHORT).show()
            finishWorkout()
        }
        btnActivarGuia.setOnClickListener { if (breathingGuideActive) stopBreathingGuide() else startBreathingGuide() }
    }

    // ─── CONTROL DE LA SESIÓN ───
    private fun startWorkout() {
        val entrenamientoId =

            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        val configuracion =

            YogaConfiguracionEntity(

                entrenamientoId =
                    entrenamientoId,

                estilo =
                    selectedStyle,

                nivel =
                    selectedLevel,

                savasanaActivo =
                    switchSavasana.isChecked,

                minutosSavasana =
                    minutosSavasana
            )

        lifecycleScope.launch {

            yogaViewModel
                .insertarConfiguracion(
                    configuracion
                )
        }
        isRunning = true
        btnIniciarCronometro.visibility = View.GONE
        cardDashboardVivo.visibility = View.VISIBLE
        layoutControlesActivos.visibility = View.VISIBLE

        tvFaseEntrenamiento.text = "🧘 EN MEDITACIÓN / YOGA"
        tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))

        // Pulso inicial
        tvPulsoDash.text = baseHeartRate.toString()

        handler.post(timerRunnable)
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

    private fun finishWorkout() {
        val entrenamientoId =

            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        val calorias =

            tvCaloriasDash
                .text
                .toString()
                .toIntOrNull()
                ?: 0

        val ciclos =

            tvCiclosDash
                .text
                .toString()
                .toIntOrNull()
                ?: 0

        val detalle =

            YogaDetalleEntity(

                entrenamientoId =
                    entrenamientoId,

                caloriasQuemadas =
                    calorias,

                ciclosRespiracion =
                    ciclos,

                duracionSegundos =
                    elapsedSeconds
            )

        lifecycleScope.launch {

            yogaViewModel
                .insertarDetalle(
                    detalle
                )
        }
        if (switchSavasana.isChecked && !isInSavasana) {
            iniciarSavasana()
        } else {
            isRunning = false
            stopBreathingGuide()
            handler.removeCallbacks(timerRunnable)
            navegarAMain()
        }
    }

    private fun iniciarSavasana() {
        isInSavasana = true
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        stopBreathingGuide()

        // Bajar los parámetros metabólicos drásticamente para la relajación
        baseHeartRate = 65
        kcalPerSecond = 0.02
        secondsPerBreath = 12

        // Ajustes visuales
        tvFaseEntrenamiento.text = "🧘 SAVASANA (RELAJACIÓN)"
        tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
        tvTimer.setTextColor(Color.parseColor("#4A90E2"))

        layoutControlesActivos.visibility = View.GONE
        btnIniciarCronometro.visibility = View.VISIBLE
        btnIniciarCronometro.text = "TERMINAR SESIÓN COMPLETAMENTE"
        btnIniciarCronometro.setOnClickListener { navegarAMain() }

        var savasanaSecondsLeft = minutosSavasana * 60
        var savasanaElapsed = 0L

        val savasanaRunnable = object : Runnable {
            override fun run() {
                if (savasanaSecondsLeft > 0) {
                    val m = savasanaSecondsLeft / 60
                    val s = savasanaSecondsLeft % 60
                    tvTimer.text = String.format("%02d:%02d", m, s)

                    savasanaSecondsLeft--
                    savasanaElapsed++
                    estimatedTotalCalories += kcalPerSecond
                    updateSimulatedDash(savasanaElapsed)

                    handler.postDelayed(this, 1000)
                } else {
                    tvTimer.text = "00:00"
                    try {
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            val pattern = VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1)
                            vibrator.vibrate(pattern)
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(longArrayOf(0, 500, 200, 500), -1)
                        }
                    } catch (e: Exception) {}
                    navegarAMain()
                }
            }
        }
        handler.post(savasanaRunnable)
    }

    private fun updateTimerUI() {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    private fun updateSimulatedDash(secondsToEvaluate: Long) {
        // Actualizar Calorías Acumuladas
        tvCaloriasDash.text = estimatedTotalCalories.toInt().toString()

        // Actualizar Pulso (solo fluctuamos cada 2 segundos para que no parezca loco)
        if (secondsToEvaluate % 2L == 0L) {
            val pulsoVivo = baseHeartRate + Random.nextInt(-3, 4)
            tvPulsoDash.text = pulsoVivo.toString()
        }

        // Actualizar Ciclos (Solo si la guía estricta 4-7-8 NO está encendida)
        if (!breathingGuideActive) {
            val naturalCycles = (secondsToEvaluate / secondsPerBreath).toInt()
            // Sumamos los que ya traíamos de la guía + los naturales actuales
            tvCiclosDash.text = (ciclosCompletados + naturalCycles).toString()
        }
    }

    // ─── GUÍA DE RESPIRACIÓN (4-7-8) ───
    private fun startBreathingGuide() {
        breathingGuideActive = true
        breathingPhase = 0
        breathingSecondsLeft = 4

        // Guardamos los ciclos que llevemos "naturales" antes de entrar a la guía estricta
        ciclosCompletados = tvCiclosDash.text.toString().toIntOrNull() ?: 0

        btnActivarGuia.text = "Detener Guía"
        btnActivarGuia.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red_1))
        btnActivarGuia.setTextColor(ContextCompat.getColor(this, R.color.white))
        handler.post(breathingRunnable)
    }

    private fun stopBreathingGuide() {
        breathingGuideActive = false
        btnActivarGuia.text = "Iniciar Guía"
        btnActivarGuia.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray_light))
        btnActivarGuia.setTextColor(ContextCompat.getColor(this, R.color.black))
        handler.removeCallbacks(breathingRunnable)
    }

    private fun updateBreathingUI() {
        val phaseText = when (breathingPhase) {
            0 -> "Inhala... ($breathingSecondsLeft)"
            1 -> "Retén... ($breathingSecondsLeft)"
            else -> "Exhala... ($breathingSecondsLeft)"
        }
        btnActivarGuia.text = phaseText
    }

    // ─── MAPA ───
    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentYoga) as SupportMapFragment?
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
        handler.removeCallbacks(timerRunnable)
        handler.removeCallbacks(breathingRunnable)
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