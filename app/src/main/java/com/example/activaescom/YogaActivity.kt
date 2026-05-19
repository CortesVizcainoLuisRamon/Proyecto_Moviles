package com.example.activaescom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout

class YogaActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    // UI
    private lateinit var tvTimer: TextView
    private lateinit var btnPausar: Button
    private lateinit var btnFinalizar: Button
    private lateinit var etNotas: EditText
    private lateinit var btnActivarGuia: Button

    // Botones de enfoque
    private lateinit var btnEnfoqueFlex: Button
    private lateinit var btnEnfoqueFuerza: Button
    private lateinit var btnEnfoqueMantra: Button
    private lateinit var btnEnfoqueRespiracion: Button

    // Cronómetro de sesión
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private var isRunning = false
    private var selectedFocus = "Flexibilidad"

    // Guía de respiración 4-7-8
    private var breathingGuideActive = false
    private var breathingPhase = 0    // 0=inhala, 1=retén, 2=exhala
    private var breathingSecondsLeft = 4

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                elapsedSeconds++
                updateTimerUI()
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val breathingRunnable = object : Runnable {
        override fun run() {
            if (breathingGuideActive) {
                breathingSecondsLeft--
                if (breathingSecondsLeft <= 0) {
                    breathingPhase = (breathingPhase + 1) % 3
                    breathingSecondsLeft = when (breathingPhase) {
                        0 -> 4   // Inhala 4s
                        1 -> 7   // Retén 7s
                        else -> 8 // Exhala 8s
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

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        setupFocusButtons()
        setupControlButtons()
        startTimer()
    }

    private fun bindViews() {
        tvTimer = findViewById(R.id.tvTimer)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        etNotas = findViewById(R.id.etNotas)
        btnActivarGuia = findViewById(R.id.btnActivarGuia)

        btnEnfoqueFlex = findViewById(R.id.btnEnfoqueFlex)
        btnEnfoqueFuerza = findViewById(R.id.btnEnfoqueFuerza)
        btnEnfoqueMantra = findViewById(R.id.btnEnfoqueMantra)
        btnEnfoqueRespiracion = findViewById(R.id.btnEnfoqueRespiracion)
    }

    private fun setupFocusButtons() {
        val focusButtons = mapOf(
            btnEnfoqueFlex to "Flexibilidad",
            btnEnfoqueFuerza to "Fuerza Espiritual",
            btnEnfoqueMantra to "Meditación / Mantra",
            btnEnfoqueRespiracion to "Pranayama (Respiración)"
        )

        focusButtons.forEach { (btn, focus) ->
            btn.setOnClickListener {
                selectedFocus = focus
                focusButtons.keys.forEach { b ->
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
            stopBreathingGuide()
            finish()
        }

        btnActivarGuia.setOnClickListener {
            if (breathingGuideActive) stopBreathingGuide() else startBreathingGuide()
        }
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

    private fun startBreathingGuide() {
        breathingGuideActive = true
        breathingPhase = 0
        breathingSecondsLeft = 4
        btnActivarGuia.text = "Detener Guía"
        handler.post(breathingRunnable)
    }

    private fun stopBreathingGuide() {
        breathingGuideActive = false
        btnActivarGuia.text = "Iniciar Guía"
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
}