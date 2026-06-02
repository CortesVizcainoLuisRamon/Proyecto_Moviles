package com.example.activaescom

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.activaescom.database.AppDatabase
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var tvTotalMes: TextView
    private lateinit var tvActividadesMes: TextView

    private lateinit var tvUltimaActividad: TextView
    private lateinit var tvUltimosDatos: TextView
    private lateinit var tvTiempoUltimo: TextView
    private lateinit var tvPorcentajeCarrera: TextView

    private lateinit var progressMeta: ProgressBar

    private lateinit var tvMetaSemanal: TextView
    private lateinit var tvMetaInfo: TextView

    private lateinit var tvPorcentajeBici: TextView

    private lateinit var chipLogro1: Chip
    private lateinit var chipLogro2: Chip
    private lateinit var chipLogro3: Chip
    private lateinit var barLunes: View
    private lateinit var barMartes: View
    private lateinit var barMiercoles: View
    private lateinit var barJueves: View
    private lateinit var barViernes: View
    private lateinit var barSabado: View
    private lateinit var barDomingo: View

    private lateinit var tvCaloriasMes: TextView
    private lateinit var tvSaludoDinamico: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )

        setContentView(R.layout.activity_main)

        findViewById<ImageButton>(
            R.id.navInicio
        ).isSelected = true

        drawerLayout =
            findViewById(R.id.drawerLayout)

        progressMeta =
            findViewById(R.id.progressMeta)

        tvMetaSemanal =
            findViewById(R.id.tvMetaSemanal)

        tvMetaInfo =
            findViewById(R.id.tvMetaInfo)

        tvPorcentajeBici =
            findViewById(R.id.tvPorcentajeBici)

        chipLogro1 =
            findViewById(R.id.chipLogro1)

        chipLogro2 =
            findViewById(R.id.chipLogro2)

        chipLogro3 =
            findViewById(R.id.chipLogro3)

        tvSaludoDinamico = findViewById(R.id.tvSaludoDinamico)

        barLunes =
            findViewById(R.id.barLunes)

        barMartes =
            findViewById(R.id.barMartes)

        barMiercoles =
            findViewById(R.id.barMiercoles)

        barJueves =
            findViewById(R.id.barJueves)

        barViernes =
            findViewById(R.id.barViernes)

        barSabado = findViewById(R.id.barSabado)

        barDomingo = findViewById(R.id.barDomingo)

        NavegacionHelper.configurarNavegacion(
            this,
            drawerLayout
        )

        // TEXTVIEWS

        tvTotalMes =
            findViewById(R.id.tvTotalMes)

        tvActividadesMes =
            findViewById(R.id.tvActividadesMes)

        tvCaloriasMes =
            findViewById(R.id.tvCaloriasMes)

        tvUltimaActividad =
            findViewById(R.id.tvUltimaActividad)

        tvUltimosDatos =
            findViewById(R.id.tvUltimosDatos)

        tvTiempoUltimo =
            findViewById(R.id.tvTiempoUltimo)

        tvPorcentajeCarrera =
            findViewById(R.id.tvPorcentajeCarrera)



       cargarDashboard()
    }

    override fun onBackPressed() {

        if (
            drawerLayout.isDrawerOpen(
                GravityCompat.START
            )
        ) {

            drawerLayout.closeDrawer(
                GravityCompat.START
            )

        } else {

            super.onBackPressed()
        }
    }

    private fun cargarDashboard() {

        lifecycleScope.launch {

            val db =
                AppDatabase.getDatabase(
                    this@MainActivity
                )

            val usuarioId =
                UserPreferences.getUsuarioId(
                    this@MainActivity
                )

            val entrenamientos = db.entrenamientoDao().obtenerEntrenamientosUsuario(usuarioId)

            val minutosPorDia =

                mutableMapOf(

                    Calendar.MONDAY to 0L,
                    Calendar.TUESDAY to 0L,
                    Calendar.WEDNESDAY to 0L,
                    Calendar.THURSDAY to 0L,
                    Calendar.FRIDAY to 0L,
                    Calendar.SATURDAY to 0L,
                    Calendar.SUNDAY to 0L
                )

            entrenamientos.forEach {

                val calendar =
                    Calendar.getInstance()

                calendar.timeInMillis =
                    it.timestamp

                val dia =
                    calendar.get(Calendar.DAY_OF_WEEK)

                val minutos =
                    it.duracionRealSegundos / 60

                minutosPorDia[dia] =

                    minutosPorDia[dia]!! + minutos
            }

            val barras = mapOf(

                Calendar.MONDAY to barLunes,
                Calendar.TUESDAY to barMartes,
                Calendar.WEDNESDAY to barMiercoles,
                Calendar.THURSDAY to barJueves,
                Calendar.FRIDAY to barViernes,
                Calendar.SATURDAY to barSabado,
                Calendar.SUNDAY to barDomingo
            )

            val maximo =

                minutosPorDia.values.maxOrNull()
                    ?: 1

            minutosPorDia.forEach {

                val dia = it.key
                val minutos = it.value

                val barra =
                    barras[dia]

                val porcentaje =

                    minutos.toFloat() /
                            maximo.toFloat()

                val altura =

                    (
                            40 + (140 * porcentaje)
                            ).toInt()

                val params =
                    barra?.layoutParams

                params?.height = altura

                barra?.layoutParams = params

                // DESTACAR DÍA MÁS ACTIVO

                if (minutos == maximo) {

                    barra?.setBackgroundColor(
                        getColor(R.color.red_900)
                    )

                } else {

                    barra?.setBackgroundColor(
                        getColor(R.color.red_300)
                    )
                }
            }

            // =====================================
            // RESUMEN GENERAL
            // =====================================

            val totalMinutos =

                entrenamientos.sumOf {

                    (
                            it.duracionRealSegundos
                            ) / 60
                }

            tvTotalMes.text =
                totalMinutos.toString()

            tvActividadesMes.text =
                entrenamientos.size.toString()

            // =====================================
            // META SEMANAL
            // =====================================

            val metaObjetivo = 600

            val progreso =

                (
                        totalMinutos * 100
                        ) / metaObjetivo

            progressMeta.progress =

                progreso.coerceAtMost(100).toInt()

            tvMetaSemanal.text =

                "$totalMinutos / $metaObjetivo min"

            val faltan =

                (
                        metaObjetivo - totalMinutos
                        ).coerceAtLeast(0)

            tvMetaInfo.text =

                if (faltan > 0) {

                    "Faltan $faltan min para completar tu meta"

                } else {

                    "🔥 Meta semanal completada"
                }

            // =====================================
            // ÚLTIMO ENTRENAMIENTO
            // =====================================

            val ultimo =
                entrenamientos.maxByOrNull {

                    it.timestamp
                }

            if (ultimo != null) {

                val emoji = when (
                    ultimo.tipoActividad
                ) {

                    "Carrera" -> "🏃"

                    "Caminata" -> "🚶"

                    "Bicicleta" -> "🚴"

                    "Natación" -> "🏊"

                    "Yoga" -> "🧘"

                    "Fuerza" -> "💪"

                    else -> "🔥"
                }

                tvUltimaActividad.text =

                    "$emoji ${ultimo.tipoActividad}"

                val minutos =

                    (
                            ultimo.duracionRealSegundos
                            ) / 60

                tvUltimosDatos.text =

                    "$minutos min • " +
                            "${ultimo.caloriasQuemadas} kcal"

                tvTiempoUltimo.text =

                    obtenerTiempoRelativo(
                        ultimo.timestamp
                    )
            }

            // =====================================
            // ACTIVIDAD FAVORITA
            // =====================================

            val total =
                entrenamientos.size

            if (total > 0) {

                val carrera =

                    entrenamientos.count {

                        it.tipoActividad ==
                                "Carrera"
                    }

                val bicicleta =

                    entrenamientos.count {

                        it.tipoActividad ==
                                "Bicicleta"
                    }

                val porcentajeCarrera =

                    (
                            carrera * 100
                            ) / total

                val porcentajeBici =

                    (
                            bicicleta * 100
                            ) / total

                tvPorcentajeCarrera.text =
                    "$porcentajeCarrera%"

                tvPorcentajeBici.text =
                    "$porcentajeBici%"
            }

            // =====================================
            // LOGROS
            // =====================================

            if (entrenamientos.size >= 7) {

                chipLogro1.text =
                    "🔥 7 entrenamientos"

            } else {

                chipLogro1.text =
                    "🏁 Sigue entrenando"
            }

            if (totalMinutos >= 1000) {

                chipLogro2.text =
                    "🏆 1000 min"

            } else {

                chipLogro2.text =
                    "⏱️ ${1000 - totalMinutos} min restantes"
            }

            val totalCalorias =

                entrenamientos.sumOf {

                    it.caloriasQuemadas
                }
            tvCaloriasMes.text =
                totalCalorias.toString()

            if (totalCalorias >= 5000) {

                chipLogro3.text =
                    "⚡ 5000 kcal"

            } else {

                chipLogro3.text =
                    "🔥 $totalCalorias kcal"
            }

            // =====================================
            // SALUDO DINÁMICO
            // =====================================

            val hora =

                java.util.Calendar.getInstance()
                    .get(
                        java.util.Calendar.HOUR_OF_DAY
                    )

            tvSaludoDinamico.text =

                when {

                    hora < 12 ->

                        "🌅 Buenos días"

                    hora < 19 ->

                        "☀️ Sigue entrenando"

                    else ->

                        "🌙 Cierra fuerte el día"
                }
        }
    }

    private fun obtenerTiempoRelativo(
        timestamp: Long
    ): String {

        val ahora =
            System.currentTimeMillis()

        val diferencia =
            ahora - timestamp

        val minutos =
            diferencia / (1000 * 60)

        val horas =
            minutos / 60

        val dias =
            horas / 24

        return when {

            minutos < 60 ->

                "Hace $minutos min"

            horas < 24 ->

                "Hace $horas horas"

            else ->

                "Hace $dias días"
        }
    }
}