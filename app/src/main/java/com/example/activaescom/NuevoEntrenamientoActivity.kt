package com.example.activaescom

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.Calendar

class NuevoEntrenamientoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var chipsActividad: List<Button>

    // Variables de control dinámico
    private lateinit var layoutMetaDistancia: LinearLayout
    private lateinit var seekBarSueno: SeekBar
    private lateinit var tvContadorSueno: TextView
    private var fechaSeleccionada: String = ""

    // VARIABLES PARA LA DURACIÓN
    private var duracionActual: Int = 30
    private lateinit var tvDuracion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_entrenamiento)

        // 1. Configuración de Navegación lateral y barra inferior
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // 2. Vincular vistas del XML
        layoutMetaDistancia = findViewById(R.id.layoutMetaDistancia)
        seekBarSueno = findViewById(R.id.seekBarSueno)
        tvContadorSueno = findViewById(R.id.tvContadorSueno)
        tvDuracion = findViewById(R.id.tvDuracion)

        val btnDuracionMenos = findViewById<Button>(R.id.btnDuracionMenos)
        val btnDuracionMas = findViewById<Button>(R.id.btnDuracionMas)

        // CORRECCIÓN AQUÍ: Se enlaza como LinearLayout para coincidir con la tarjeta maestra
        val layoutFecha = findViewById<LinearLayout>(R.id.layoutFecha)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)

        // 3. FUNCIONALIDAD: Aumentar / Disminuir Duración (Intervalos de 5 minutos)
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

        // 4. Configuración del SeekBar de sueño
        seekBarSueno.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvContadorSueno.text = "$progress hrs"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 5. Configuración del Calendario para la Fecha
        val calendarioSistema = Calendar.getInstance()
        val anio = calendarioSistema.get(Calendar.YEAR)
        val mes = calendarioSistema.get(Calendar.MONTH)
        val dia = calendarioSistema.get(Calendar.DAY_OF_MONTH)

        fechaSeleccionada = String.format("%02d/%02d/%d", dia, mes + 1, anio)
        tvFecha.text = "Hoy, $fechaSeleccionada"

        layoutFecha.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                fechaSeleccionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                tvFecha.text = fechaSeleccionada
            }, anio, mes, dia)
            datePickerDialog.show()
        }

        // 6. Configuración de los Chips de Deportes (Incluyendo Caminata)
        val btnChipCarrera = findViewById<Button>(R.id.btnChipCarrera)
        val btnChipCaminata = findViewById<Button>(R.id.btnChipCaminata)
        val btnChipBicicleta = findViewById<Button>(R.id.btnChipBicicleta)
        val btnChipFuerza = findViewById<Button>(R.id.btnChipFuerza)
        val btnChipYoga = findViewById<Button>(R.id.btnChipYoga)
        val btnChipNatacion = findViewById<Button>(R.id.btnChipNatacion)

        chipsActividad = listOf(btnChipCarrera, btnChipCaminata, btnChipBicicleta, btnChipFuerza, btnChipYoga, btnChipNatacion)

        for (chip in chipsActividad) {
            chip.setOnClickListener { botonSeleccionado ->
                seleccionarActividad(botonSeleccionado as Button)
            }
        }
    }

    /**
     * Actualiza el TextView visual del tiempo estimado
     */
    private fun actualizarTextoDuracion() {
        tvDuracion.text = "$duracionActual min"
    }

    /**
     * Alterna colores de los chips y oculta/muestra la meta de km de forma fluida
     */
    private fun seleccionarActividad(botonSeleccionado: Button) {
        for (chip in chipsActividad) {
            if (chip == botonSeleccionado) {
                chip.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_1)
                chip.setTextColor(ContextCompat.getColor(this, R.color.white))

                // Colapso dinámico de los kilómetros y su línea divisora interna
                if (chip.id == R.id.btnChipYoga || chip.id == R.id.btnChipFuerza) {
                    layoutMetaDistancia.visibility = View.GONE
                } else {
                    layoutMetaDistancia.visibility = View.VISIBLE
                }
            } else {
                chip.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
                chip.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
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
}