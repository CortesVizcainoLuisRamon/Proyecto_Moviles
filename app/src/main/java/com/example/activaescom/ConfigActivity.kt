package com.example.activaescom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File
import java.util.Calendar
import androidx.work.*
import java.util.concurrent.TimeUnit

class ConfigActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout

    // ─────────────────────────────────────────────
    // PROGRAMAR RECORDATORIO
    // ─────────────────────────────────────────────

    private fun programarRecordatorio() {

        val workRequest =

            PeriodicWorkRequestBuilder<
                    RecordatorioWorker
                    >(24, TimeUnit.HOURS)

                .build()

        WorkManager
            .getInstance(this)

            .enqueueUniquePeriodicWork(

                "recordatorio_entrenamiento",

                ExistingPeriodicWorkPolicy.UPDATE,

                workRequest
            )

        Toast.makeText(

            this,

            "Recordatorio activado",

            Toast.LENGTH_SHORT

        ).show()
    }

    // ─────────────────────────────────────────────
    // ELIMINAR CUENTA
    // ─────────────────────────────────────────────

    private fun mostrarDialogoConfirmacionPassword() {

        val inputPassword =
            android.widget.EditText(this).apply {

                inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

                hint =
                    "Ingresa tu contraseña"

                setPadding(
                    50,
                    50,
                    50,
                    50
                )
            }

        val container =
            android.widget.FrameLayout(this)

        val params =
            android.widget.FrameLayout.LayoutParams(

                android.view.ViewGroup.LayoutParams.MATCH_PARENT,

                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )

        params.setMargins(
            50,
            20,
            50,
            0
        )

        inputPassword.layoutParams =
            params

        container.addView(
            inputPassword
        )

        AlertDialog.Builder(this)

            .setTitle(
                "Confirmar Identidad"
            )

            .setMessage(
                "Para proteger tu cuenta, ingresa tu contraseña actual para confirmar la eliminación."
            )

            .setView(
                container
            )

            .setPositiveButton("Confirmar") { _, _ ->

                val passwordIngresada =
                    inputPassword.text.toString().trim()

                if (
                    passwordIngresada.isNotEmpty()
                ) {

                    Toast.makeText(

                        this,

                        "Cuenta eliminada con éxito",

                        Toast.LENGTH_SHORT

                    ).show()

                    UserPreferences.cerrarSesion(
                        this
                    )

                    val intent =
                        Intent(
                            this,
                            LoginActivity::class.java
                        )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                    finish()

                } else {

                    Toast.makeText(

                        this,

                        "Contraseña incorrecta",

                        Toast.LENGTH_LONG

                    ).show()
                }
            }

            .setNegativeButton(
                "Cancelar",
                null
            )

            .show()
    }

    // ─────────────────────────────────────────────
    // ON CREATE
    // ─────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_config
        )

        // ─────────────────────────────────────────
        // PERMISO NOTIFICACIONES
        // ─────────────────────────────────────────

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (

                ActivityCompat.checkSelfPermission(

                    this,

                    Manifest.permission.POST_NOTIFICATIONS

                ) != PackageManager.PERMISSION_GRANTED

            ) {

                ActivityCompat.requestPermissions(

                    this,

                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),

                    1001
                )
            }
        }

        findViewById<android.widget.ImageButton>(
            R.id.navConfig
        ).isSelected = true

        // ─────────────────────────────────────────
        // RECORDATORIOS
        // ─────────────────────────────────────────

        val switchRecordatorio =
            findViewById<androidx.appcompat.widget.SwitchCompat>(
                R.id.switchRecordatorio
            )

        val layoutConfigHora =
            findViewById<LinearLayout>(
                R.id.layoutConfigHora
            )

        val tvHoraSeleccionada =
            findViewById<TextView>(
                R.id.tvHoraSeleccionada
            )

        switchRecordatorio.isChecked =
            UserPreferences.getRecordatorioActivo(this)

        tvHoraSeleccionada.text =
            UserPreferences.getHoraRecordatorio(this)

        layoutConfigHora.visibility =

            if (switchRecordatorio.isChecked)
                View.VISIBLE
            else
                View.GONE

        switchRecordatorio.setOnCheckedChangeListener {

                _,
                isChecked ->

            UserPreferences.saveRecordatorioActivo(
                this,
                isChecked
            )

            layoutConfigHora.visibility =

                if (isChecked)
                    View.VISIBLE
                else
                    View.GONE
        }

        tvHoraSeleccionada.setOnClickListener {

            val calendario =
                Calendar.getInstance()

            val horaActual =
                calendario.get(
                    Calendar.HOUR_OF_DAY
                )

            val minutoActual =
                calendario.get(
                    Calendar.MINUTE
                )

            val timePickerDialog =
                android.app.TimePickerDialog(

                    this,

                    { _,
                      horaSeleccionada,
                      minutoSeleccionado ->

                        val amPm =
                            if (horaSeleccionada >= 12)
                                "PM"
                            else
                                "AM"

                        val horaFormat =
                            if (horaSeleccionada % 12 == 0)
                                12
                            else
                                horaSeleccionada % 12

                        val minutoFormat =
                            String.format(
                                "%02d",
                                minutoSeleccionado
                            )

                        val horaTexto =
                            "$horaFormat:$minutoFormat $amPm"

                        tvHoraSeleccionada.text =
                            horaTexto

                        UserPreferences.saveHoraRecordatorio(
                            this,
                            horaTexto
                        )

                        programarRecordatorio(
                        )
                    },

                    horaActual,

                    minutoActual,

                    false
                )

            timePickerDialog.show()
        }

        // ─────────────────────────────────────────
        // DRAWER
        // ─────────────────────────────────────────

        drawerLayout =
            findViewById(
                R.id.drawerLayout
            )

        NavegacionHelper.configurarNavegacion(
            this,
            drawerLayout
        )

        cargarDatosUsuario()

        // ─────────────────────────────────────────
        // BOTONES
        // ─────────────────────────────────────────

        findViewById<LinearLayout>(
            R.id.btnHistorial
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HistorialActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(
            R.id.btnConfigPerfil
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    PerfilActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(R.id.btnPrivacidad).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    PrivacidadActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(R.id.btnTerminos).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    TerminosActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(R.id.btnContacto).setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ContactoActivity::class.java
                )
            )
        }

        findViewById<LinearLayout>(
            R.id.btnCerrarSesion
        ).setOnClickListener {

            AlertDialog.Builder(this)

                .setTitle(
                    "Cerrar sesión"
                )

                .setMessage(
                    "¿Deseas salir de EntrenaIPN?"
                )

                .setPositiveButton(
                    "Sí"
                ) { _, _ ->

                    val intent =
                        Intent(
                            this,
                            LoginActivity::class.java
                        )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                    finish()
                }

                .setNegativeButton(
                    "Cancelar",
                    null
                )

                .show()
        }

        findViewById<LinearLayout>(
            R.id.btnEliminarCuenta
        ).setOnClickListener {

            mostrarDialogoConfirmacionPassword()
        }

        // ─────────────────────────────────────────
        // VALORACIÓN
        // ─────────────────────────────────────────

        val myRatingBar =
            findViewById<RatingBar>(
                R.id.myRatingBar
            )

        val tvRateResult =
            findViewById<TextView>(
                R.id.tvRateResult
            )

        val btnEnviar = findViewById<Button>(R.id.btnEnviarValoracion)

        val yaValoro =

            UserPreferences
                .getValoracionRealizada(this)

        if (yaValoro) {

            btnEnviar.isEnabled = false

            btnEnviar.alpha = 0.5f

            myRatingBar.setIsIndicator(true)

            tvRateResult.text =
                "Ya has valorado EntrenaIPN 😎🔥"
        }

        btnEnviar.setOnClickListener {

            val calificacion =
                myRatingBar.rating

            if (calificacion == 0f) {

                Toast.makeText(

                    this,

                    "Selecciona al menos una estrella",

                    Toast.LENGTH_SHORT

                ).show()

            } else {

                tvRateResult.text =
                    "¡Gracias por tu valoración!"

                Toast.makeText(

                    this,

                    "¡Valoración enviada!",

                    Toast.LENGTH_SHORT

                ).show()

                UserPreferences
                    .guardarValoracionRealizada(
                        this,
                        true
                    )

                btnEnviar.isEnabled =
                    false

                btnEnviar.alpha =
                    0.5f

                myRatingBar.setIsIndicator(
                    true
                )
            }
        }
    }

    // ─────────────────────────────────────────────
    // DATOS USUARIO
    // ─────────────────────────────────────────────

    override fun onResume() {

        super.onResume()

        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {

        val tvUsuario =
            findViewById<TextView>(
                R.id.tvConfigUsuario
            )

        val tvEmail =
            findViewById<TextView>(
                R.id.tvConfigEmail
            )

        val usuarioGuardado =
            UserPreferences.getUsuario(this)

        val emailGuardado =
            UserPreferences.getEmail(this)

        if (usuarioGuardado.isNotEmpty())
            tvUsuario.text = usuarioGuardado

        if (emailGuardado.isNotEmpty())
            tvEmail.text = emailGuardado

        val ruta =
            UserPreferences.getFotoPerfil(this)

        if (ruta.isNotEmpty()) {

            val archivo =
                File(ruta)

            if (archivo.exists()) {

                val imgConfigPerfil =
                    findViewById<ImageView>(
                        R.id.imgConfigPerfil
                    )

                imgConfigPerfil.setImageBitmap(
                    BitmapFactory.decodeFile(ruta)
                )

                imgConfigPerfil.clearColorFilter()
            }
        }
    }

    // ─────────────────────────────────────────────
    // BACK
    // ─────────────────────────────────────────────

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
}