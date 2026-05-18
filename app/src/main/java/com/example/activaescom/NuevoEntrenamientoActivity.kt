package com.example.activaescom

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.Calendar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NuevoEntrenamientoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var chipsActividad: List<Button>
    private lateinit var botonesLugar: List<Button> // NUEVO: Lista para los botones de ubicación

    // Variables de control dinámico
    private lateinit var layoutMetaDistancia: LinearLayout
    private lateinit var seekBarSueno: SeekBar
    private lateinit var tvContadorSueno: TextView
    private lateinit var etOtroLugarInput: EditText // NUEVO: Input para lugar personalizado
    private var fechaSeleccionada: String = ""

    // VARIABLES PARA LA DURACIÓN
    private var duracionActual: Int = 30
    private lateinit var tvDuracion: TextView

    // NUEVO: Variables para almacenar las selecciones actuales de cara al Intent
    private var actividadSeleccionada: String = "Carrera"
    private var lugarSeleccionado: String = "Casa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_entrenamiento)

        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // 1. Configuración de Navegación lateral y barra inferior
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // 2. Vincular vistas del XML
        layoutMetaDistancia = findViewById(R.id.layoutMetaDistancia)
        seekBarSueno = findViewById(R.id.seekBarSueno)
        tvContadorSueno = findViewById(R.id.tvContadorSueno)
        tvDuracion = findViewById(R.id.tvDuracion)
        etOtroLugarInput = findViewById(R.id.etOtroLugarInput) // NUEVO

        val btnDuracionMenos = findViewById<Button>(R.id.btnDuracionMenos)
        val btnDuracionMas = findViewById<Button>(R.id.btnDuracionMas)
        val layoutFecha = findViewById<LinearLayout>(R.id.layoutFecha)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)

        // NUEVO: Vistas restantes del formulario para recopilar los datos finales
        val etNombreRutina = findViewById<EditText>(R.id.etNombreRutina)
        val etDistanciaInput = findViewById<EditText>(R.id.etDistanciaInput)
        val etNotas = findViewById<EditText>(R.id.etNotas)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val btnIniciarEntrenamiento = findViewById<Button>(R.id.btnIniciarEntrenamiento)

        val rootView = findViewById<View>(R.id.drawerLayout)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom

            if (keypadHeight > screenHeight * 0.15) {
                // 👇 Solo hace scroll si el foco está en etNotas
                if (etNotas.hasFocus()) {
                    scrollView.setPadding(0, 0, 0, keypadHeight / 3)
                    scrollView.postDelayed({
                        scrollView.smoothScrollTo(0, scrollView.getChildAt(0).height)
                    }, 100)
                }
            } else {
                scrollView.setPadding(0, 0, 0, 0)
            }
        }


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

        // 7. NUEVO: Configuración e inicialización de los Botones de Lugar
        val btnLugarPista = findViewById<Button>(R.id.btnLugarPista) // Casa
        val btnLugarGimnasio = findViewById<Button>(R.id.btnLugarGimnasio) // Parque
        val btnLugarExterior = findViewById<Button>(R.id.btnLugarExterior) // Pista
        val btnLugarOtro = findViewById<Button>(R.id.btnLugarOtro) // Otro

        botonesLugar = listOf(btnLugarPista, btnLugarGimnasio, btnLugarExterior, btnLugarOtro)

        for (boton in botonesLugar) {
            boton.setOnClickListener { botonSeleccionado ->
                seleccionarLugar(botonSeleccionado as Button)
            }
        }

        // 8. NUEVO: Enviar toda la información armada a CarreraActivity al dar clic (CON VALIDACIONES)
        btnIniciarEntrenamiento.setOnClickListener {
            val nombreInput = etNombreRutina.text.toString().trim()
            val distanciaInput = etDistanciaInput.text.toString().trim()
            val otroLugarTxt = etOtroLugarInput.text.toString().trim()

            // 1. VALIDACIÓN: Nombre de la rutina obligatorio
            if (nombreInput.isEmpty()) {
                etNombreRutina.error = "El nombre de la rutina es obligatorio"
                etNombreRutina.requestFocus()
                return@setOnClickListener // Detiene la ejecución y no pasa a la siguiente pantalla
            }

            // 2. VALIDACIÓN: Distancia obligatoria (solo si el contenedor de km está visible)
            if (layoutMetaDistancia.visibility == View.VISIBLE && distanciaInput.isEmpty()) {
                etDistanciaInput.error = "Debes ingresar los kilómetros"
                etDistanciaInput.requestFocus()
                return@setOnClickListener
            }

            // 3. VALIDACIÓN: Lugar personalizado obligatorio (solo si se seleccionó la opción "Otro")
            if (etOtroLugarInput.visibility == View.VISIBLE && otroLugarTxt.isEmpty()) {
                etOtroLugarInput.error = "Por favor, especifica el lugar de entrenamiento"
                etOtroLugarInput.requestFocus()
                return@setOnClickListener
            }

            // SI PASA TODAS LAS VALIDACIONES ANTERIORES, PROCEDE A NAVEGAR
            // ✅ Navega a la pantalla correcta según el chip de actividad elegido
            val destino: Class<*> = when (actividadSeleccionada) {
                "Carrera"   -> CarreraActivity::class.java
                "Caminata"  -> CaminataActivity::class.java
                "Bicicleta" -> BicicletaActivity::class.java
                // Fuerza, Yoga y Natación: cuando tengan su pantalla, agrégalas aquí
                else        -> CarreraActivity::class.java
            }
            val intent = Intent(this, destino)

            val distanciaFinal = if (layoutMetaDistancia.visibility == View.VISIBLE) {
                "$distanciaInput km"
            } else {
                "0.0 km"
            }

            val lugarFinal = if (etOtroLugarInput.visibility == View.VISIBLE) {
                otroLugarTxt
            } else {
                lugarSeleccionado
            }

            intent.putExtra("NOMBRE_RUTINA", nombreInput)
            intent.putExtra("META_DISTANCIA", distanciaFinal)
            intent.putExtra("HORAS_SUENO", seekBarSueno.progress)
            intent.putExtra("LUGAR_ENTRENAMIENTO", lugarFinal)
            intent.putExtra("DURACION_ESTIMADA", duracionActual)
            intent.putExtra("NOTAS", etNotas.text.toString().trim())
            intent.putExtra("FECHA_ENTRENAMIENTO", fechaSeleccionada)

            startActivity(intent)
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
        actividadSeleccionada = botonSeleccionado.text.toString()
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

    /**
     * NUEVO: Alterna colores de los botones de lugar y maneja la visibilidad de "Otro lugar"
     */
    private fun seleccionarLugar(botonSeleccionado: Button) {
        for (boton in botonesLugar) {
            if (boton == botonSeleccionado) {
                boton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red_1)
                boton.setTextColor(ContextCompat.getColor(this, R.color.white))

                // Si selecciona "Otro", muestra el EditText y le da el foco de escritura
                if (boton.id == R.id.btnLugarOtro) {
                    etOtroLugarInput.visibility = View.VISIBLE
                    etOtroLugarInput.requestFocus()
                } else {
                    // Si cambia a una opción fija, esconde el campo y limpia lo escrito
                    etOtroLugarInput.visibility = View.GONE
                    etOtroLugarInput.text.clear()
                    lugarSeleccionado = boton.text.toString()
                }
            } else {
                // Configura los botones no seleccionados con el color gris del XML original
                boton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_light)
                boton.setTextColor(ContextCompat.getColor(this, R.color.gray_text))
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