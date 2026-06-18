package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activaescom.adapter.HistorialAdapter
import com.example.activaescom.model.HistorialUniversalItem
import com.example.activaescom.viewmodel.BicicletaViewModel
import com.example.activaescom.viewmodel.CaminataViewModel
import com.example.activaescom.viewmodel.EntrenamientoViewModel
import com.example.activaescom.viewmodel.FuerzaViewModel
import com.example.activaescom.viewmodel.NatacionViewModel
import com.example.activaescom.viewmodel.YogaViewModel
import kotlinx.coroutines.launch

class HistorialActivity : BaseActivity() {

    private lateinit var entrenamientoViewModel: EntrenamientoViewModel
    private lateinit var yogaViewModel: YogaViewModel
    private lateinit var fuerzaViewModel: FuerzaViewModel
    private lateinit var natacionViewModel: NatacionViewModel
    private lateinit var bicicletaViewModel: BicicletaViewModel
    private lateinit var caminataViewModel: CaminataViewModel

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: HistorialAdapter
    private var listaUniversalCompleta = mutableListOf<HistorialUniversalItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        findViewById<ImageButton>(R.id.navConfig)?.isSelected = true

        findViewById<ImageButton>(R.id.navInicio)?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.navPerfil)?.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.navConfig)?.setOnClickListener {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
        }

        entrenamientoViewModel = EntrenamientoViewModel(application)
        yogaViewModel = YogaViewModel(application)
        fuerzaViewModel = FuerzaViewModel(application)
        natacionViewModel = NatacionViewModel(application)
        bicicletaViewModel = BicicletaViewModel(application)
        caminataViewModel = CaminataViewModel(application)

        recycler = findViewById(R.id.recyclerHistorial)
        recycler.layoutManager = LinearLayoutManager(this@HistorialActivity)

        adapter = HistorialAdapter(mutableListOf())
        recycler.adapter = adapter

        findViewById<Button>(R.id.btnFiltroTodos).setOnClickListener { aplicarFiltro("Todos") }
        findViewById<Button>(R.id.btnFiltroCarrera).setOnClickListener { aplicarFiltro("Carrera") }
        findViewById<Button>(R.id.btnFiltroCaminata).setOnClickListener { aplicarFiltro("Caminata") }
        findViewById<Button>(R.id.btnFiltroBicicleta).setOnClickListener { aplicarFiltro("Bicicleta") }
        findViewById<Button>(R.id.btnFiltroFuerza).setOnClickListener { aplicarFiltro("Fuerza") }
        findViewById<Button>(R.id.btnFiltroYoga).setOnClickListener { aplicarFiltro("Yoga") }
        findViewById<Button>(R.id.btnFiltroNatacion).setOnClickListener { aplicarFiltro("Natación") }

        cargarEntrenamientos()
    }

    private fun aplicarFiltro(tipo: String) {
        val listaFiltrada = if (tipo == "Todos") {
            listaUniversalCompleta
        } else {
            listaUniversalCompleta.filter { it.tipoActividad == tipo }
        }
        adapter = HistorialAdapter(listaFiltrada.toMutableList())
        recycler.adapter = adapter
    }

    private fun cargarEntrenamientos() {
        lifecycleScope.launch {
            val usuarioId = UserPreferences.getUsuarioId(this@HistorialActivity)
            listaUniversalCompleta.clear()

            val carreras = entrenamientoViewModel.obtenerCarrerasCompletas(usuarioId)
            val yogas = yogaViewModel.obtenerYogaCompleto(usuarioId)
            val fuerzas = fuerzaViewModel.obtenerFuerzaCompleta(usuarioId)
            val nataciones = natacionViewModel.obtenerNatacionCompleta(usuarioId)
            val bicicletas = bicicletaViewModel.obtenerBicicletaCompleta(usuarioId)
            val caminatas = caminataViewModel.obtenerCaminataCompleta(usuarioId)

            carreras.forEach { carrera ->
                val entrenamiento = carrera.entrenamiento
                val config = carrera.configuracion
                val detalle = carrera.detalle

                val resumen = "distancia recorrida: ${String.format("%.2f", detalle?.distanciaKm ?: 0.0)} km • ${detalle?.caloriasQuemadas ?: 0} kcal"
                val detalles = """
📍 Lugar: ${entrenamiento.lugarEntrenamiento}
💤 Sueño: ${entrenamiento.horasSueno} hrs
🎯 Objetivo: ${config?.objetivo ?: "No registrado"}
❤️ Zona: ${config?.zonaCardiaca ?: "No registrada"}
🛣 Superficie: ${config?.superficie ?: "No registrada"}
🔥 Calentamiento: ${if (config?.calentamientoActivo == true) "Sí" else "No"}
⚡ Ritmo: ${detalle?.ritmoPromedio ?: "0"}
🚶 Pasos: ${detalle?.pasos ?: 0}
📝 Notas: ${if (entrenamiento.notas.isNotBlank()) entrenamiento.notas else "Sin notas"}
""".trimIndent()

                if (detalle != null) {
                    listaUniversalCompleta.add(
                        HistorialUniversalItem(
                            tipoActividad = entrenamiento.tipoActividad,
                            nombreEntrenamiento = entrenamiento.nombreEntrenamiento,
                            fecha = entrenamiento.fechaEntrenamiento,
                            resumen = resumen,
                            detalles = detalles,
                            timestamp = entrenamiento.timestamp,
                            rutaMapa = entrenamiento.rutaMapa
                        )
                    )
                }
            }

            yogas.forEach { yoga ->
                val entrenamiento = yoga.entrenamiento
                val config = yoga.configuracion
                val detalle = yoga.detalle

                val resumen = "${detalle?.duracionSegundos?.div(60) ?: 0} min • ${detalle?.caloriasQuemadas ?: 0} kcal"
                val detalles = """
📍 Lugar: ${entrenamiento.lugarEntrenamiento}
💤 Sueño: ${entrenamiento.horasSueno} hrs
🧘 Estilo: ${config?.estilo ?: "No registrado"}
📈 Nivel: ${config?.nivel ?: "No registrado"}
🌬 Respiraciones: ${detalle?.ciclosRespiracion ?: 0}
🛌 Savasana: ${if (config?.savasanaActivo == true) "Sí" else "No"}
⏳ Minutos Savasana: ${config?.minutosSavasana ?: 0}
📝 Notas: ${if (entrenamiento.notas.isNotBlank()) entrenamiento.notas else "Sin notas"}
""".trimIndent()

                if (detalle != null) {
                    listaUniversalCompleta.add(
                        HistorialUniversalItem(
                            tipoActividad = entrenamiento.tipoActividad,
                            nombreEntrenamiento = entrenamiento.nombreEntrenamiento,
                            fecha = entrenamiento.fechaEntrenamiento,
                            resumen = resumen,
                            detalles = detalles,
                            timestamp = entrenamiento.timestamp,
                            rutaMapa = entrenamiento.rutaMapa
                        )
                    )
                }
            }

            fuerzas.forEach { fuerza ->
                val entrenamiento = fuerza.entrenamiento
                val config = fuerza.configuracion
                val detalle = fuerza.detalle

                val resumen = "${detalle?.seriesObjetivo ?: 0} series • ${detalle?.pesoObjetivo ?: 0} kg"
                val detalles = """
📍 Lugar: ${entrenamiento.lugarEntrenamiento}
💤 Sueño: ${entrenamiento.horasSueno} hrs
💪 Grupo muscular: ${config?.grupoMuscular ?: "No registrado"}
🔥 Calentamiento: ${if (config?.calentamientoActivo == true) "Sí" else "No"}
⏳ Minutos calentamiento: ${config?.minutosCalentamiento ?: 0}
🏋 Series: ${detalle?.seriesObjetivo ?: 0}
🔁 Repeticiones: ${detalle?.repeticionesObjetivo ?: 0}
⚖ Peso: ${detalle?.pesoObjetivo ?: 0} kg
📊 Volumen total: ${detalle?.volumenTotal ?: 0}
📝 Notas: ${if (entrenamiento.notas.isNotBlank()) entrenamiento.notas else "Sin notas"}
""".trimIndent()

                if (detalle != null) {
                    listaUniversalCompleta.add(
                        HistorialUniversalItem(
                            tipoActividad = entrenamiento.tipoActividad,
                            nombreEntrenamiento = entrenamiento.nombreEntrenamiento,
                            fecha = entrenamiento.fechaEntrenamiento,
                            resumen = resumen,
                            detalles = detalles,
                            timestamp = entrenamiento.timestamp,
                            rutaMapa = entrenamiento.rutaMapa
                        )
                    )
                }
            }

            nataciones.forEach { natacion ->
                val entrenamiento = natacion.entrenamiento
                val config = natacion.configuracion
                val detalle = natacion.detalle

                val resumen = "${detalle?.distanciaKm ?: 0} km • ${detalle?.caloriasQuemadas ?: 0} kcal"
                val detalles = """
📍 Lugar: ${entrenamiento.lugarEntrenamiento}
💤 Sueño: ${entrenamiento.horasSueno} hrs
🏊 Estilo: ${config?.estilo ?: "No registrado"}
📈 Nivel: ${config?.nivel ?: "No registrado"}
🏟 Alberca: ${config?.tamanoAlberca ?: 0} m
🔥 Calentamiento: ${if (config?.calentamientoActivo == true) "Sí" else "No"}
⏳ Minutos calentamiento: ${config?.minutosCalentamiento ?: 0}
🏊 Vueltas: ${detalle?.vueltasTotales ?: 0}
📏 Distancia: ${detalle?.distanciaKm ?: 0} km
🔥 Calorías: ${detalle?.caloriasQuemadas ?: 0}
⏱ Duración: ${detalle?.duracionSegundos?.div(60) ?: 0} min
📝 Notas: ${if (entrenamiento.notas.isNotBlank()) entrenamiento.notas else "Sin notas"}
""".trimIndent()

                if (detalle != null) {
                    listaUniversalCompleta.add(
                        HistorialUniversalItem(
                            tipoActividad = entrenamiento.tipoActividad,
                            nombreEntrenamiento = entrenamiento.nombreEntrenamiento,
                            fecha = entrenamiento.fechaEntrenamiento,
                            resumen = resumen,
                            detalles = detalles,
                            timestamp = entrenamiento.timestamp,
                            rutaMapa = entrenamiento.rutaMapa
                        )
                    )
                }
            }

            bicicletas.forEach { bicicleta ->
                val entrenamiento = bicicleta.entrenamiento
                val config = bicicleta.configuracion
                val detalle = bicicleta.detalle

                val resumen = "distancia recorrida: ${String.format("%.2f", detalle?.distanciaKm ?: 0.0)} km • ${detalle?.caloriasQuemadas ?: 0} kcal"
                val detalles = """
📍 Lugar: ${entrenamiento.lugarEntrenamiento}
💤 Sueño: ${entrenamiento.horasSueno} hrs
🚴 Tipo bicicleta: ${config?.tipoBicicleta ?: "No registrado"}
🛣 Terreno: ${config?.terreno ?: "No registrado"}
🎯 Objetivo: ${config?.objetivo ?: "No registrado"}
⚡ E-Bike: ${if (config?.esEBike == true) "Sí" else "No"}
🔥 Calentamiento: ${if (config?.calentamientoActivo == true) "Sí" else "No"}
⏳ Minutos calentamiento: ${config?.minutosCalentamiento ?: 0}
📏 Distancia: ${detalle?.distanciaKm ?: 0} km
🚴 Velocidad promedio: ${detalle?.velocidadPromedio ?: 0} km/h
🔥 Calorías: ${detalle?.caloriasQuemadas ?: 0}
⏱ Duración: ${detalle?.duracionSegundos?.div(60) ?: 0} min
🚨 Avisos: ${config?.avisosRuta ?: "No registrados"}
📝 Notas: ${if (entrenamiento.notas.isNotBlank()) entrenamiento.notas else "Sin notas"}
""".trimIndent()

                if (detalle != null) {
                    listaUniversalCompleta.add(
                        HistorialUniversalItem(
                            tipoActividad = entrenamiento.tipoActividad,
                            nombreEntrenamiento = entrenamiento.nombreEntrenamiento,
                            fecha = entrenamiento.fechaEntrenamiento,
                            resumen = resumen,
                            detalles = detalles,
                            timestamp = entrenamiento.timestamp,
                            rutaMapa = entrenamiento.rutaMapa
                        )
                    )
                }
            }

            caminatas.forEach { caminata ->
                val entrenamiento = caminata.entrenamiento
                val config = caminata.configuracion
                val detalle = caminata.detalle

                val resumen = "distancia recorrida: ${String.format("%.2f", detalle?.distanciaKm ?: 0.0)} km • ${detalle?.caloriasQuemadas ?: 0} kcal"
                val detalles = """
📍 Lugar: ${entrenamiento.lugarEntrenamiento}
💤 Sueño: ${entrenamiento.horasSueno} hrs
🚶 Intensidad: ${config?.intensidadPaso ?: "No registrada"}
🛣 Terreno: ${config?.terreno ?: "No registrado"}
🔥 Calentamiento: ${if (config?.calentamientoActivo == true) "Sí" else "No"}
⏳ Minutos calentamiento: ${config?.minutosCalentamiento ?: 0}
👥 Acompañante: ${config?.acompanante ?: "Ninguno"}
📏 Distancia: ${detalle?.distanciaKm ?: 0} km
⚡ Ritmo: ${detalle?.ritmo ?: "0"}
🔥 Calorías: ${detalle?.caloriasQuemadas ?: 0}
⏱ Duración: ${detalle?.duracionSegundos?.div(60) ?: 0} min
📝 Notas: ${if (entrenamiento.notas.isNotBlank()) entrenamiento.notas else "Sin notas"}
""".trimIndent()

                Log.d("HISTORIAL", "Agregando item: ${entrenamiento.nombreEntrenamiento}")

                if (detalle != null) {
                    listaUniversalCompleta.add(
                        HistorialUniversalItem(
                            tipoActividad = entrenamiento.tipoActividad,
                            nombreEntrenamiento = entrenamiento.nombreEntrenamiento,
                            fecha = entrenamiento.fechaEntrenamiento,
                            resumen = resumen,
                            detalles = detalles,
                            timestamp = entrenamiento.timestamp,
                            rutaMapa = entrenamiento.rutaMapa
                        )
                    )
                }
            }

            listaUniversalCompleta.sortByDescending { it.timestamp }
            adapter = HistorialAdapter(listaUniversalCompleta)
            recycler.adapter = adapter
        }
    }
}