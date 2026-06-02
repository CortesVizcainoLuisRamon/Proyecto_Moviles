package com.example.activaescom

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.RadioButton
import android.widget.RadioGroup
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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.activaescom.database.entities.NatacionConfiguracionEntity
import com.example.activaescom.database.entities.NatacionDetalleEntity
import com.example.activaescom.viewmodel.NatacionViewModel
import com.example.activaescom.database.AppDatabase
import android.widget.Toast

class NatacionActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var natacionViewModel: NatacionViewModel

    // UI - Dashboard y Controles Inferiores
    private lateinit var tvTimer: TextView
    private lateinit var tvDistancia: TextView
    private lateinit var tvCalorias: TextView
    private lateinit var tvFaseEntrenamiento: TextView
    private lateinit var cardDashboardVivo: CardView

    private lateinit var btnIniciarCronometro: MaterialButton
    private lateinit var layoutControlesActivos: LinearLayout
    private lateinit var btnPausar: MaterialButton
    private lateinit var btnFinalizar: MaterialButton

    // UI - Tarjetas Superiores y Configuración
    private lateinit var btnContarVuelta: Button
    private lateinit var tvVueltasContador: TextView
    private lateinit var tvVueltasEstimadas: TextView

    // Mapa
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private lateinit var tvGpsEstado: TextView

    // Configuración de alberca y niveles
    private lateinit var rgTamanoAlberca: RadioGroup
    private lateinit var rbAlberca25: RadioButton
    private lateinit var rbAlberca50: RadioButton

    private lateinit var btnNivelPrincipiante: Button
    private lateinit var btnNivelIntermedio: Button
    private lateinit var btnNivelAvanzado: Button

    private lateinit var btnEstiloCrol: Button
    private lateinit var btnEstiloPecho: Button
    private lateinit var btnEstiloDorso: Button
    private lateinit var btnEstiloMariposa: Button

    // Calentamiento
    private lateinit var switchCalentamiento: SwitchCompat
    private lateinit var layoutDuracionCalentamiento: LinearLayout
    private lateinit var botonesCalentamiento: List<Button>
    private var minutosCalentamiento: Int = 5

    // Variables de Estado y Simulación
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var isWarmingUp = false
    private var warmupLimitSeconds = 0L
    private var elapsedSeconds = 0L // Segundos de calentamiento
    private var workoutSeconds = 0L // Segundos nadando

    private var selectedStyle = "Crol (Libre)"
    private var metaMetros = 1000

    // Velocidad simulada en m/s (Por defecto Intermedio: ~1:45/100m)
    private var velocidadMetrosPorSegundo = 0.95

    private val poolLength: Int
        get() = if (rbAlberca25.isChecked) 25 else 50

    companion object {
        private const val REQUEST_LOCATION = 101
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                if (isWarmingUp) {
                    elapsedSeconds++
                    if (elapsedSeconds >= warmupLimitSeconds) {
                        transicionANatacion()
                    } else {
                        updateTimerUI(elapsedSeconds)
                    }
                } else {
                    workoutSeconds++
                    updateTimerUI(workoutSeconds)
                    updateSimulatedStats()
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

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

        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_natacion)

        natacionViewModel =
            NatacionViewModel(
                application
            )

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        cargarDatosConfiguracion()
        setupConfiguraciones()
        setupControlButtons()

        // Inicializar mapa (Solo ubicación, sin trazado de ruta)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initMap()
    }

    private fun bindViews() {
        tvGpsEstado = findViewById(R.id.tvGpsEstado)

        // Controles de Sesión
        btnIniciarCronometro = findViewById(R.id.btnIniciarCronometro)
        layoutControlesActivos = findViewById(R.id.layoutControlesActivos)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        cardDashboardVivo = findViewById(R.id.cardDashboardVivo)

        // Dashboard Vivo (Inferior)
        tvFaseEntrenamiento = findViewById(R.id.tvFaseEntrenamiento)
        tvTimer = findViewById(R.id.tvTimer)
        tvDistancia = findViewById(R.id.tvDistancia)
        tvCalorias = findViewById(R.id.tvCalorias)

        // Tarjetas Superiores
        btnContarVuelta = findViewById(R.id.btnContarVuelta)
        tvVueltasContador = findViewById(R.id.tvVueltasContador)
        tvVueltasEstimadas = findViewById(R.id.tvVueltasEstimadas)

        // Configuración de Alberca y Niveles
        rgTamanoAlberca = findViewById(R.id.rgTamanoAlberca)
        rbAlberca25 = findViewById(R.id.rbAlberca25)
        rbAlberca50 = findViewById(R.id.rbAlberca50)

        btnNivelPrincipiante = findViewById(R.id.btnNivelPrincipiante)
        btnNivelIntermedio = findViewById(R.id.btnNivelIntermedio)
        btnNivelAvanzado = findViewById(R.id.btnNivelAvanzado)

        btnEstiloCrol = findViewById(R.id.btnEstiloCrol)
        btnEstiloPecho = findViewById(R.id.btnEstiloPecho)
        btnEstiloDorso = findViewById(R.id.btnEstiloDorso)
        btnEstiloMariposa = findViewById(R.id.btnEstiloMariposa)

        // Calentamiento
        switchCalentamiento = findViewById(R.id.switchCalentamiento)
        layoutDuracionCalentamiento = findViewById(R.id.layoutDuracionCalentamiento)
        botonesCalentamiento = listOf(
            findViewById(R.id.btnCal5),
            findViewById(R.id.btnCal10),
            findViewById(R.id.btnCal15)
        )
    }

    // ─────────────────────────────────────────────
    // CARGAR DETALLES PLANIFICADOS Y MÚSICA
    // ─────────────────────────────────────────────
    private fun cargarDatosConfiguracion() {
        findViewById<TextView>(R.id.tvConfigRutinaTitulo).text =
            intent.getStringExtra("NOMBRE_RUTINA") ?: "Sesión de Natación"

        val metaStr = intent.getStringExtra("META_DISTANCIA") ?: "1000 m"
        findViewById<TextView>(R.id.tvConfigMeta).text = "Meta: $metaStr"

        // Extraer los metros de la cadena (Ej. de "1000 m" a 1000)
        metaMetros = metaStr.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 1000

        findViewById<TextView>(R.id.tvConfigSueno).text =
            "💤 : ${intent.getIntExtra("HORAS_SUENO", 7)} hrs"
        findViewById<TextView>(R.id.tvConfigLugar).text =
            "📍 : ${intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Alberca"}"
        findViewById<TextView>(R.id.tvConfigDuracion).text =
            "⏱️ : ${intent.getIntExtra("DURACION_ESTIMADA", 45)} min"
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

        // Configurar botones de Música
        findViewById<LinearLayout>(R.id.btnPlataformaSpotify).setOnClickListener { abrirPlataforma("https://open.spotify.com", "com.spotify.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaYoutube).setOnClickListener { abrirPlataforma("https://music.youtube.com", "com.google.android.apps.youtube.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaApple).setOnClickListener { abrirPlataforma("https://music.apple.com", "com.apple.android.music") }
        findViewById<LinearLayout>(R.id.btnPlataformaAmazon).setOnClickListener { abrirPlataforma("https://music.amazon.com", "com.amazon.mp3") }
        findViewById<LinearLayout>(R.id.btnPlataformaDeezer).setOnClickListener { abrirPlataforma("https://www.deezer.com", "deezer.android.app") }
    }

    private fun abrirPlataforma(urlWeb: String, packageName: String) {
        val intentApp = packageManager.getLaunchIntentForPackage(packageName)
        if (intentApp != null) {
            startActivity(intentApp)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb)))
        }
    }

    // ─────────────────────────────────────────────
    // CONFIGURACIÓN DE ALBERCA, NIVELES, ESTILOS Y CALENTAMIENTO
    // ─────────────────────────────────────────────
    private fun setupConfiguraciones() {
        // Cálculo inicial de vueltas
        calcularVueltasMeta()
        rgTamanoAlberca.setOnCheckedChangeListener { _, _ ->
            calcularVueltasMeta()
            if(isRunning && !isWarmingUp) updateSimulatedStats()
        }

        // Niveles de Experiencia
        val nivelesButtons = listOf(btnNivelPrincipiante, btnNivelIntermedio, btnNivelAvanzado)
        btnNivelPrincipiante.setOnClickListener {
            velocidadMetrosPorSegundo = 0.66 // Aprox 2:30/100m
            seleccionarChip(btnNivelPrincipiante, nivelesButtons)
        }
        btnNivelIntermedio.setOnClickListener {
            velocidadMetrosPorSegundo = 0.95 // Aprox 1:45/100m
            seleccionarChip(btnNivelIntermedio, nivelesButtons)
        }
        btnNivelAvanzado.setOnClickListener {
            velocidadMetrosPorSegundo = 1.33 // Aprox 1:15/100m
            seleccionarChip(btnNivelAvanzado, nivelesButtons)
        }

        // Estilos
        val styleButtons = listOf(btnEstiloCrol, btnEstiloPecho, btnEstiloDorso, btnEstiloMariposa)
        btnEstiloCrol.setOnClickListener { selectedStyle = "Crol"; seleccionarChip(btnEstiloCrol, styleButtons) }
        btnEstiloPecho.setOnClickListener { selectedStyle = "Pecho"; seleccionarChip(btnEstiloPecho, styleButtons) }
        btnEstiloDorso.setOnClickListener { selectedStyle = "Dorso"; seleccionarChip(btnEstiloDorso, styleButtons) }
        btnEstiloMariposa.setOnClickListener { selectedStyle = "Mariposa"; seleccionarChip(btnEstiloMariposa, styleButtons) }

        // Calentamiento
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

    private fun calcularVueltasMeta() {
        val vueltas = metaMetros / poolLength
        tvVueltasEstimadas.text = "Necesitas $vueltas vueltas (largos) para alcanzar tu meta"
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

    private fun setupControlButtons() {
        btnIniciarCronometro.setOnClickListener { startWorkout() }
        btnPausar.setOnClickListener { if (isRunning) pauseWorkout() else resumeWorkout() }
        btnFinalizar.setOnClickListener {
            android.widget.Toast.makeText(this, "¡Entrenamiento finalizado con éxito!", android.widget.Toast.LENGTH_SHORT).show()
            finishWorkout()
        }
        btnContarVuelta.setOnClickListener { registrarVueltaManual() }
    }

    // ─────────────────────────────────────────────
    // LÓGICA DE LA SESIÓN Y SIMULACIÓN
    // ─────────────────────────────────────────────
    private fun startWorkout() {
        val entrenamientoId =

            intent.getIntExtra(
                "ENTRENAMIENTO_ID",
                -1
            )

        val tamanoAlberca =

            if (rbAlberca25.isChecked)
                25
            else
                50

        val nivel =

            when {

                btnNivelPrincipiante.backgroundTintList ==
                        ContextCompat.getColorStateList(
                            this,
                            R.color.red_1
                        ) -> "Principiante"

                btnNivelAvanzado.backgroundTintList ==
                        ContextCompat.getColorStateList(
                            this,
                            R.color.red_1
                        ) -> "Avanzado"

                else -> "Intermedio"
            }

        val minutosCalentamientoFinal =

            if (switchCalentamiento.isChecked)
                minutosCalentamiento
            else
                0

        val configuracion =

            NatacionConfiguracionEntity(

                entrenamientoId =
                    entrenamientoId,

                tamanoAlberca =
                    tamanoAlberca,

                nivel =
                    nivel,

                estilo =
                    selectedStyle,

                calentamientoActivo =
                    switchCalentamiento.isChecked,

                minutosCalentamiento =
                    minutosCalentamientoFinal
            )

        lifecycleScope.launch {

            natacionViewModel
                .insertarConfiguracion(
                    configuracion
                )
        }
        isRunning = true
        btnIniciarCronometro.visibility = View.GONE
        cardDashboardVivo.visibility = View.VISIBLE
        layoutControlesActivos.visibility = View.VISIBLE

        if (switchCalentamiento.isChecked) {
            isWarmingUp = true
            warmupLimitSeconds = minutosCalentamiento * 60L
            elapsedSeconds = 0L
            workoutSeconds = 0L

            tvFaseEntrenamiento.text = "🔥 CALENTAMIENTO FUERA DEL AGUA"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            tvTimer.setTextColor(Color.parseColor("#FF8C00"))
        } else {
            isWarmingUp = false
            workoutSeconds = 0L

            tvFaseEntrenamiento.text = "🏊 EN NATACIÓN ($selectedStyle)"
            tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
            tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        }

        handler.post(timerRunnable)
    }

    private fun transicionANatacion() {
        isWarmingUp = false
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
        } catch (e: Exception) {}

        tvFaseEntrenamiento.text = "🏊 EN NATACIÓN ($selectedStyle)"
        tvFaseEntrenamiento.setTextColor(ContextCompat.getColor(this, R.color.red_1))
        tvTimer.setTextColor(ContextCompat.getColor(this, R.color.red_1))

        // Limpiar stats en UI
        tvDistancia.text = "0.0"
        tvCalorias.text = "0"
        tvVueltasContador.text = "0"
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

    private fun finishWorkout() { val entrenamientoId = intent.getIntExtra("ENTRENAMIENTO_ID", -1)
        val calorias =

            tvCalorias
                .text
                .toString()
                .toIntOrNull()
                ?: 0

        val distanciaKm =

            tvDistancia
                .text
                .toString()
                .toDoubleOrNull()
                ?: 0.0

        val vueltasTotales =

            tvVueltasContador
                .text
                .toString()
                .toIntOrNull()
                ?: 0

        val detalle =

            NatacionDetalleEntity(

                entrenamientoId =
                    entrenamientoId,

                distanciaKm =
                    distanciaKm,

                vueltasTotales =
                    vueltasTotales,

                caloriasQuemadas =
                    calorias,

                duracionSegundos =
                    workoutSeconds
            )

        lifecycleScope.launch {

            natacionViewModel
                .insertarDetalle(
                    detalle
                )
            AppDatabase
                .getDatabase(this@NatacionActivity)
                .entrenamientoDao()
                .actualizarResultados(
                    entrenamientoId,
                    workoutSeconds,
                    calorias
                )

        }
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        navegarAMain()
    }

    private fun registrarVueltaManual() {
        if (isRunning && !isWarmingUp) {
            // Avanza el tiempo virtualmente para simular que se completó un largo
            val tiempoParaUnLargo = (poolLength / velocidadMetrosPorSegundo).toLong()
            workoutSeconds += tiempoParaUnLargo
            updateSimulatedStats()
        }
    }

    private fun updateTimerUI(segundos: Long) {
        val h = segundos / 3600
        val m = (segundos % 3600) / 60
        val s = segundos % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    private fun updateSimulatedStats() {
        val distanciaM = workoutSeconds * velocidadMetrosPorSegundo
        val distanciaKm = distanciaM / 1000.0
        val vueltasActuales = (distanciaM / poolLength).toInt()

        tvVueltasContador.text = vueltasActuales.toString()
        tvDistancia.text = "${distanciaM.toInt()} m"

        // Calorías quemadas estimadas (Aprox. 250 kcal por km en natación)
        val calorias = (distanciaKm * 250).toInt()
        tvCalorias.text = calorias.toString()

        // Ritmo / 100m
        if (workoutSeconds > 0 && distanciaM > 0) {
            val secsPor100m = (workoutSeconds.toDouble() / distanciaM) * 100
            val minP100 = (secsPor100m / 60).toInt()
            val secP100 = (secsPor100m % 60).toInt()

            val ritmoFormateado = String.format("%d'%02d\"", minP100, secP100)
        }
    }

    // ─────────────────────────────────────────────
    // MAPA (Solo ubicación, sin trazado)
    // ─────────────────────────────────────────────
    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragmentNatacion) as SupportMapFragment?
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

            // Habilitar el círculo azul en el mapa
            googleMap?.isMyLocationEnabled = true

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 17f))
                }
            }
        } else {
            tvGpsEstado.text = "📍 Buscando GPS..."
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showInitialLocation()
        }
    }

    // ─────────────────────────────────────────────
    // CICLO DE VIDA
    // ─────────────────────────────────────────────
    override fun onDestroy() {
        super.onDestroy()
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