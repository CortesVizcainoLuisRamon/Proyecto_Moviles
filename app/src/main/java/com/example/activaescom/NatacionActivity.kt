package com.example.activaescom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout

class NatacionActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    // UI
    private lateinit var tvTimer: TextView
    private lateinit var btnPausar: Button
    private lateinit var btnFinalizar: Button
    private lateinit var btnContarVuelta: Button
    private lateinit var etNotas: EditText

    // Estadísticas
    private lateinit var tvVueltasContador: TextView
    private lateinit var tvDistanciaMetros: TextView
    private lateinit var tvRitmoNatacion: TextView

    // Configuración de alberca y estilos
    private lateinit var rgTamanoAlberca: RadioGroup
    private lateinit var rbAlberca25: RadioButton
    private lateinit var rbAlberca50: RadioButton
    private lateinit var btnEstiloCrol: Button
    private lateinit var btnEstiloPecho: Button
    private lateinit var btnEstiloDorso: Button
    private lateinit var btnEstiloMariposa: Button

    // Cronómetro
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private var isRunning = false
    private var vueltas = 0
    private var selectedStyle = "Crol (Libre)"

    private val poolLength: Int
        get() = if (rbAlberca25.isChecked) 25 else 50

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
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_natacion)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        setupSwimStyleButtons()
        setupControlButtons()
        startTimer()
    }

    private fun bindViews() {
        tvTimer = findViewById(R.id.tvTimer)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        btnContarVuelta = findViewById(R.id.btnContarVuelta)
        etNotas = findViewById(R.id.etNotas)

        rgTamanoAlberca = findViewById(R.id.rgTamanoAlberca)
        rbAlberca25 = findViewById(R.id.rbAlberca25)
        rbAlberca50 = findViewById(R.id.rbAlberca50)

        btnEstiloCrol = findViewById(R.id.btnEstiloCrol)
        btnEstiloPecho = findViewById(R.id.btnEstiloPecho)
        btnEstiloDorso = findViewById(R.id.btnEstiloDorso)
        btnEstiloMariposa = findViewById(R.id.btnEstiloMariposa)

        tvVueltasContador = findViewById(R.id.tvVueltasContador)
        tvDistanciaMetros = findViewById(R.id.tvDistanciaMetros)
        tvRitmoNatacion = findViewById(R.id.tvRitmoNatacion)
    }

    private fun setupSwimStyleButtons() {
        val styleButtons = mapOf(
            btnEstiloCrol to "Crol (Libre)",
            btnEstiloPecho to "Pecho (Braza)",
            btnEstiloDorso to "Espalda / Dorso",
            btnEstiloMariposa to "Mariposa"
        )

        styleButtons.forEach { (btn, style) ->
            btn.setOnClickListener {
                selectedStyle = style
                styleButtons.keys.forEach { b ->
                    b.backgroundTintList = resources.getColorStateList(R.color.gray_light, theme)
                    b.setTextColor(resources.getColor(R.color.gray_text, theme))
                }
                btn.backgroundTintList = resources.getColorStateList(R.color.red_1, theme)
                btn.setTextColor(resources.getColor(R.color.white, theme))
            }
        }
    }

    private fun setupControlButtons() {
        btnPausar.setOnClickListener {
            if (isRunning) pauseTimer() else resumeTimer()
        }

        btnFinalizar.setOnClickListener {
            stopTimer()
            finish()
        }

        btnContarVuelta.setOnClickListener { registrarVuelta() }

        rgTamanoAlberca.setOnCheckedChangeListener { _, _ -> updateStatsUI() }
    }

    private fun registrarVuelta() {
        vueltas++
        updateStatsUI()
    }

    private fun startTimer() {
        isRunning = true
        handler.post(timerRunnable)
    }

    private fun pauseTimer() {
        isRunning = false
        btnPausar.text = "Reanudar"
        handler.removeCallbacks(timerRunnable)
    }

    private fun resumeTimer() {
        isRunning = true
        btnPausar.text = "Pausar"
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimerUI() {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    private fun updateStatsUI() {
        tvVueltasContador.text = vueltas.toString()

        val distanciaM = vueltas * poolLength
        val distanciaKm = distanciaM / 1000.0
        tvDistanciaMetros.text = if (distanciaM >= 1000) String.format("%.2f km", distanciaKm) else "$distanciaM"

        if (vueltas > 0 && elapsedSeconds > 0) {
            val secsPorVuelta = elapsedSeconds.toDouble() / vueltas
            val minPV = (secsPorVuelta / 60).toInt()
            val secPV = (secsPorVuelta % 60).toInt()
            tvRitmoNatacion.text = String.format("%d'%02d\"", minPV, secPV)
        }
    }

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
}