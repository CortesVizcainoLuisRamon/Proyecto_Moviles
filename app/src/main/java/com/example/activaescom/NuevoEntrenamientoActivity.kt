package com.example.activaescom

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope

import java.util.Calendar

import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.viewmodel.EntrenamientoViewModel

import kotlinx.coroutines.launch

class NuevoEntrenamientoActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var entrenamientoViewModel: EntrenamientoViewModel
    private lateinit var chipsActividad: List<Button>
    private lateinit var botonesLugar: List<Button>
    private lateinit var botonesEnfoqueYoga: List<Button>
    private lateinit var botonesEnfoqueFuerza: List<Button>

    private lateinit var layoutMetaDistancia: LinearLayout
    private lateinit var cardEnfoqueYoga: CardView
    private lateinit var cardEnfoqueFuerza: CardView
    private lateinit var tvMetaDistanciaLabel: TextView
    private lateinit var tvUnidadDistancia: TextView
    private lateinit var seekBarSueno: SeekBar
    private lateinit var tvContadorSueno: TextView
    private lateinit var etOtroLugarInput: EditText

    private var fechaSeleccionada: String = ""
    private var duracionActual: Int = 30
    private lateinit var tvDuracion: TextView
    private var unidadDistanciaActual: String = "km"

    private var actividadSeleccionada: String = "Carrera"
    private var lugarSeleccionado: String = "Casa"
    private var enfoqueYogaSeleccionado: String = "Flexibilidad"
    private var enfoqueFuerzaSeleccionado: String = "Hipertrofia (Volumen)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_entrenamiento)

        entrenamientoViewModel = EntrenamientoViewModel(application)
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        findViewById<ImageButton>(R.id.navInicio)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.navPerfil)?.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.navConfig)?.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
            finish()
        }

        layoutMetaDistancia = findViewById(R.id.layoutMetaDistancia)
        cardEnfoqueYoga = findViewById(R.id.cardEnfoqueYoga)
        cardEnfoqueFuerza = findViewById(R.id.cardEnfoqueFuerza)
        tvMetaDistanciaLabel = findViewById(R.id.tvMetaDistanciaLabel)
        tvUnidadDistancia = findViewById(R.id.tvUnidadDistancia)
        seekBarSueno = findViewById(R.id.seekBarSueno)
        tvContadorSueno = findViewById(R.id.tvContadorSueno)
        tvDuracion = findViewById(R.id.tvDuracion)
        etOtroLugarInput = findViewById(R.id.etOtroLugarInput)

        val btnDuracionMenos = findViewById<Button>(R.id.btnDuracionMenos)
        val btnDuracionMas = findViewById<Button>(R.id.btnDuracionMas)
        val layoutFecha = findViewById<LinearLayout>(R.id.layoutFecha)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val etNombreRutina = findViewById<EditText>(R.id.etNombreRutina)
        val etDistanciaInput = findViewById<EditText>(R.id.etDistanciaInput)
        val etNotas = findViewById<EditText>(R.id.etNotas)
        val btnIniciarEntrenamiento = findViewById<Button>(R.id.btnIniciarEntrenamiento)

        btnDuracionMas.setOnClickListener {
            if (duracionActual < 300) {
                duracionActual += 5
                actualizarTextoDuracion()
            }
        }

        btnDuracionMenos.setOnClickListener {
            if (duracionActual > 5) {
                duracionActual -= 5
                actualizarTextoDuracion()
            }
        }

        seekBarSueno.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvContadorSueno.text = "$progress hrs"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val calendarioSistema = Calendar.getInstance()
        val anio = calendarioSistema.get(Calendar.YEAR)
        val mes = calendarioSistema.get(Calendar.MONTH)
        val dia = calendarioSistema.get(Calendar.DAY_OF_MONTH)

        fechaSeleccionada = String.format("%02d/%02d/%d", dia, mes + 1, anio)
        tvFecha.text = "Hoy, $fechaSeleccionada"

        layoutFecha.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                tvFecha.text = fechaSeleccionada
            }, anio, mes, dia).show()
        }

        val btnChipCarrera = findViewById<Button>(R.id.btnChipCarrera)
        val btnChipCaminata = findViewById<Button>(R.id.btnChipCaminata)
        val btnChipBicicleta = findViewById<Button>(R.id.btnChipBicicleta)
        val btnChipFuerza = findViewById<Button>(R.id.btnChipFuerza)
        val btnChipYoga = findViewById<Button>(R.id.btnChipYoga)
        val btnChipNatacion = findViewById<Button>(R.id.btnChipNatacion)

        chipsActividad = listOf(btnChipCarrera, btnChipCaminata, btnChipBicicleta, btnChipFuerza, btnChipYoga, btnChipNatacion)

        val btnEnfoqueFlex = findViewById<Button>(R.id.btnEnfoqueFlex)
        val btnEnfoqueMantra = findViewById<Button>(R.id.btnEnfoqueMantra)
        val btnEnfoqueRespiracion = findViewById<Button>(R.id.btnEnfoqueRespiracion)

        botonesEnfoqueYoga = listOf(btnEnfoqueFlex, btnEnfoqueMantra, btnEnfoqueRespiracion)

        for (boton in botonesEnfoqueYoga) {
            boton.setOnClickListener {
                enfoqueYogaSeleccionado = boton.text.toString()
                seleccionarBoton(boton, botonesEnfoqueYoga)
            }
        }

        val btnFuerzaHipertrofia = findViewById<Button>(R.id.btnFuerzaHipertrofia)
        val btnFuerzaMaxima = findViewById<Button>(R.id.btnFuerzaMaxima)
        val btnFuerzaResistencia = findViewById<Button>(R.id.btnFuerzaResistencia)
        val btnFuerzaPotencia = findViewById<Button>(R.id.btnFuerzaPotencia)

        botonesEnfoqueFuerza = listOf(btnFuerzaHipertrofia, btnFuerzaMaxima, btnFuerzaResistencia, btnFuerzaPotencia)

        for (boton in botonesEnfoqueFuerza) {
            boton.setOnClickListener {
                enfoqueFuerzaSeleccionado = boton.text.toString()
                seleccionarBoton(boton, botonesEnfoqueFuerza)
            }
        }

        for (chip in chipsActividad) {
            chip.setOnClickListener { seleccionarActividad(it as Button) }
        }

        val btnLugarPista = findViewById<Button>(R.id.btnLugarPista)
        val btnLugarGimnasio = findViewById<Button>(R.id.btnLugarGimnasio)
        val btnLugarExterior = findViewById<Button>(R.id.btnLugarExterior)
        val btnLugarOtro = findViewById<Button>(R.id.btnLugarOtro)

        botonesLugar = listOf(btnLugarPista, btnLugarGimnasio, btnLugarExterior, btnLugarOtro)

        for (boton in botonesLugar) {
            boton?.setOnClickListener { seleccionarLugar(it as Button) }
        }

        btnIniciarEntrenamiento.setOnClickListener {
            val nombreEntrenamiento = etNombreRutina.text.toString().trim()

            if (nombreEntrenamiento.isBlank()) {
                etNombreRutina.error = "Ingresa un nombre"
                etNombreRutina.requestFocus()
                return@setOnClickListener
            }

            val notas = etNotas.text.toString().trim()
            val usuarioId = UserPreferences.getUsuarioId(this)

            if (etOtroLugarInput.visibility == View.VISIBLE) {
                val lugarPersonalizado = etOtroLugarInput.text.toString().trim()
                if (lugarPersonalizado.isNotBlank()) {
                    lugarSeleccionado = lugarPersonalizado
                }
            }

            val metaKm = etDistanciaInput.text.toString().toDoubleOrNull() ?: 5.0

            val entrenamiento = EntrenamientoEntity(
                usuarioId = usuarioId,
                tipoActividad = actividadSeleccionada,
                nombreEntrenamiento = nombreEntrenamiento,
                fechaEntrenamiento = fechaSeleccionada,
                lugarEntrenamiento = lugarSeleccionado,
                horasSueno = seekBarSueno.progress,
                duracionEstimada = duracionActual,
                metaKmObjetivo = metaKm,
                notas = notas,
                duracionRealSegundos = 0,
                caloriasQuemadas = 0,
                rutaMapa = null,
                timestamp = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                val entrenamientoId = entrenamientoViewModel.insertarEntrenamiento(entrenamiento)
                val intent = when (actividadSeleccionada) {
                    "Yoga" -> Intent(this@NuevoEntrenamientoActivity, YogaActivity::class.java)
                    "Carrera" -> Intent(this@NuevoEntrenamientoActivity, CarreraActivity::class.java)
                    "Caminata" -> Intent(this@NuevoEntrenamientoActivity, CaminataActivity::class.java)
                    "Bicicleta" -> Intent(this@NuevoEntrenamientoActivity, BicicletaActivity::class.java)
                    "Fuerza" -> Intent(this@NuevoEntrenamientoActivity, FuerzaActivity::class.java)
                    "Natación" -> Intent(this@NuevoEntrenamientoActivity, NatacionActivity::class.java)
                    else -> null
                }

                intent?.apply {
                    putExtra("ENFOQUE_YOGA", enfoqueYogaSeleccionado)
                    putExtra("ENFOQUE_FUERZA", enfoqueFuerzaSeleccionado)
                    putExtra("ENTRENAMIENTO_ID", entrenamientoId.toInt())
                    putExtra("META_KM", metaKm)
                    putExtra("META_DISTANCIA", if (actividadSeleccionada == "Natación") "${metaKm.toInt()} m" else "$metaKm km")
                    putExtra("NOMBRE_RUTINA", nombreEntrenamiento)
                    putExtra("FECHA_ENTRENAMIENTO", fechaSeleccionada)
                    putExtra("LUGAR_ENTRENAMIENTO", lugarSeleccionado)
                    putExtra("HORAS_SUENO", seekBarSueno.progress)
                    putExtra("DURACION_ESTIMADA", duracionActual)
                    putExtra("NOTAS", notas)
                    startActivity(this)
                    finish()
                }
            }
        }

        seleccionarActividad(btnChipCarrera)
    }

    private fun actualizarTextoDuracion() {
        tvDuracion.text = "$duracionActual min"
    }

    private fun seleccionarActividad(botonSeleccionado: Button) {
        actividadSeleccionada = botonSeleccionado.text.toString()

        for (chip in chipsActividad) {
            if (chip == botonSeleccionado) {
                chip.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_1)
                chip.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                chip.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                chip.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            }
        }

        when (actividadSeleccionada) {
            "Natación" -> {
                layoutMetaDistancia.visibility = View.VISIBLE
                cardEnfoqueYoga.visibility = View.GONE
                cardEnfoqueFuerza.visibility = View.GONE
                tvMetaDistanciaLabel.text = "Meta distancia (m)"
                tvUnidadDistancia.text = "m"
                unidadDistanciaActual = "m"
            }
            "Yoga" -> {
                layoutMetaDistancia.visibility = View.GONE
                cardEnfoqueYoga.visibility = View.VISIBLE
                cardEnfoqueFuerza.visibility = View.GONE
            }
            "Fuerza" -> {
                layoutMetaDistancia.visibility = View.GONE
                cardEnfoqueYoga.visibility = View.GONE
                cardEnfoqueFuerza.visibility = View.VISIBLE
            }
            else -> {
                tvMetaDistanciaLabel.text = "Meta distancia (km)"
                tvUnidadDistancia.text = "km"
                unidadDistanciaActual = "km"
                layoutMetaDistancia.visibility = View.VISIBLE
                cardEnfoqueYoga.visibility = View.GONE
                cardEnfoqueFuerza.visibility = View.GONE
            }
        }
    }

    private fun seleccionarLugar(botonSeleccionado: Button) {
        for (boton in botonesLugar) {
            if (boton == botonSeleccionado) {
                boton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_1)
                boton.setTextColor(ContextCompat.getColor(this, R.color.white))

                if (boton.id == R.id.btnLugarOtro) {
                    etOtroLugarInput.visibility = View.VISIBLE
                    etOtroLugarInput.requestFocus()
                } else {
                    etOtroLugarInput.visibility = View.GONE
                    etOtroLugarInput.text.clear()
                    lugarSeleccionado = boton.text.toString()
                }
            } else {
                boton?.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_light)
                boton?.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun seleccionarBoton(seleccionado: Button, grupo: List<Button>) {
        for (boton in grupo) {
            if (boton == seleccionado) {
                boton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_1)
                boton.setTextColor(ContextCompat.getColor(this, R.color.white))
            } else {
                boton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                boton.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
            }
        }
    }
}