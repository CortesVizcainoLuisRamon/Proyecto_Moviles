package com.example.activaescom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout

class FuerzaActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    // UI
    private lateinit var tvTimer: TextView
    private lateinit var btnPausar: Button
    private lateinit var btnFinalizar: Button
    private lateinit var etNotas: EditText
    private lateinit var tvSeriesComp: TextView
    private lateinit var tvRepsTotales: TextView
    private lateinit var tvVolumen: TextView

    // Botones musculares
    private lateinit var btnPecho: Button
    private lateinit var btnEspalda: Button
    private lateinit var btnPiernas: Button
    private lateinit var btnBrazos: Button
    private lateinit var btnHombros: Button
    private lateinit var btnRegistrarSerie: Button

    // Inputs
    private lateinit var etSeries: EditText
    private lateinit var etReps: EditText
    private lateinit var etPeso: EditText

    // Cronómetro
    private val handler = Handler(Looper.getMainLooper())
    private var elapsedSeconds = 0L
    private var isRunning = false
    private var seriesCount = 0
    private var totalReps = 0
    private var totalVolume = 0.0
    private var selectedMuscleGroup = "Pecho"

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
        setContentView(R.layout.activity_fuerza)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        bindViews()
        setupMuscleGroupButtons()
        setupControlButtons()

        // El cronómetro inicia automáticamente
        startTimer()
    }

    private fun bindViews() {
        tvTimer = findViewById(R.id.tvTimer)
        btnPausar = findViewById(R.id.btnPausar)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        etNotas = findViewById(R.id.etNotas)

        tvSeriesComp = findViewById(R.id.tvSeriesComp)
        tvRepsTotales = findViewById(R.id.tvRepsTotales)
        tvVolumen = findViewById(R.id.tvVolumen)

        btnPecho = findViewById(R.id.btnPecho)
        btnEspalda = findViewById(R.id.btnEspalda)
        btnPiernas = findViewById(R.id.btnPiernas)
        btnBrazos = findViewById(R.id.btnBrazos)
        btnHombros = findViewById(R.id.btnHombros)
        btnRegistrarSerie = findViewById(R.id.btnRegistrarSerie)

        etSeries = findViewById(R.id.etSeries)
        etReps = findViewById(R.id.etReps)
        etPeso = findViewById(R.id.etPeso)
    }

    private fun setupMuscleGroupButtons() {
        val muscleButtons = mapOf(
            btnPecho to "Pecho",
            btnEspalda to "Espalda",
            btnPiernas to "Piernas",
            btnBrazos to "Brazos",
            btnHombros to "Hombros"
        )

        muscleButtons.forEach { (btn, muscle) ->
            btn.setOnClickListener {
                selectedMuscleGroup = muscle
                muscleButtons.keys.forEach { b ->
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

        btnRegistrarSerie.setOnClickListener { registrarSerie() }
    }

    private fun registrarSerie() {
        val repsText = etReps.text.toString()
        val pesoText = etPeso.text.toString()

        if (repsText.isNotEmpty() && pesoText.isNotEmpty()) {
            val reps = repsText.toInt()
            val peso = pesoText.toDouble()

            seriesCount++
            totalReps += reps
            totalVolume += (reps * peso)

            tvSeriesComp.text = seriesCount.toString()
            tvRepsTotales.text = totalReps.toString()
            tvVolumen.text = String.format("%.1f", totalVolume)

            Toast.makeText(this, "Serie $seriesCount registrada — $selectedMuscleGroup", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Ingresa repeticiones y peso", Toast.LENGTH_SHORT).show()
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