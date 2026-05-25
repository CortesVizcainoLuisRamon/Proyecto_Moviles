package com.example.activaescom

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File
import android.view.View
import com.example.activaescom.LoginActivity

class ConfigActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout

    private fun mostrarDialogoConfirmacionPassword() {
        // 1. Creamos un campo de texto programáticamente
        val inputPassword = android.widget.EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Ingresa tu contraseña"
            setPadding(50, 50, 50, 50)
        }

        // 2. Creamos un contenedor para darle márgenes al EditText y que no se vea pegado a los bordes
        val container = android.widget.FrameLayout(this)
        val params = android.widget.FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(50, 20, 50, 0)
        inputPassword.layoutParams = params
        container.addView(inputPassword)

        // 3. Mostramos el segundo cuadro de diálogo
        AlertDialog.Builder(this)
            .setTitle("Confirmar Identidad")
            .setMessage("Para proteger tu cuenta, ingresa tu contraseña actual para confirmar la eliminación.")
            .setView(container)
            .setPositiveButton("Confirmar") { _, _ ->
                val passwordIngresada = inputPassword.text.toString().trim()

                // ⚠️ AQUÍ DEBES VALIDAR LA CONTRASEÑA REAL ⚠️
                // Supongamos que tienes un UserPreferences.getPassword(this) o validas contra tu base de datos (Firebase, SQLite, etc.)
                // Ejemplo: val passwordGuardada = UserPreferences.getPassword(this)

                if (passwordIngresada.isNotEmpty() /* && passwordIngresada == passwordGuardada */) {

                    // Si la contraseña es correcta, procedemos con la eliminación
                    Toast.makeText(this, "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show()

                    // Aquí iría tu código para limpiar UserPreferences o borrar de la BD

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // Si la contraseña es incorrecta o está vacía
                    Toast.makeText(this, "Contraseña incorrecta. Operación cancelada.", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        findViewById<android.widget.ImageButton>(R.id.navConfig).isSelected = true

        // ── LÓGICA DE RECORDATORIO DIARIO ──
        val switchRecordatorio = findViewById<androidx.appcompat.widget.SwitchCompat>(R.id.switchRecordatorio)
        val layoutConfigHora = findViewById<LinearLayout>(R.id.layoutConfigHora)
        val tvHoraSeleccionada = findViewById<TextView>(R.id.tvHoraSeleccionada)

// 1. Mostrar/Ocultar el panel de la hora dependiendo del Switch
        switchRecordatorio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                layoutConfigHora.visibility = View.VISIBLE
            } else {
                layoutConfigHora.visibility = View.GONE
            }
        }

// 2. Abrir el reloj para elegir la hora en formato AM/PM
        tvHoraSeleccionada.setOnClickListener {
            // Obtenemos la hora actual como base para el reloj
            val calendario = java.util.Calendar.getInstance()
            val horaActual = calendario.get(java.util.Calendar.HOUR_OF_DAY)
            val minutoActual = calendario.get(java.util.Calendar.MINUTE)

            val timePickerDialog = android.app.TimePickerDialog(this, { _, horaSeleccionada, minutoSeleccionado ->

                // Formatear la hora a formato 12 horas (AM/PM)
                val amPm = if (horaSeleccionada >= 12) "PM" else "AM"
                val horaFormat = if (horaSeleccionada % 12 == 0) 12 else horaSeleccionada % 12
                val minutoFormat = String.format("%02d", minutoSeleccionado)

                // Actualizamos el texto en pantalla
                tvHoraSeleccionada.text = "$horaFormat:$minutoFormat $amPm"

            }, horaActual, minutoActual, false) // <-- "false" fuerza el reloj a modo 12 horas (AM/PM)

            timePickerDialog.show()
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // Cargamos los datos del usuario logueado
        cargarDatosUsuario()

        findViewById<LinearLayout>(R.id.btnHistorial).setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        // ── 1. AJUSTES: Ir a Perfil ──
        findViewById<LinearLayout>(R.id.btnConfigPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // ── 2. AJUSTES: Privacidad de Datos ──
        findViewById<LinearLayout>(R.id.btnPrivacidad).setOnClickListener {
            startActivity(Intent(this, PrivacidadActivity::class.java))
        }

        // ── 3. AJUSTES: Cerrar sesión (Con diálogo de confirmación de UX) ──
        findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas salir de EntrenaIPN?")
                .setPositiveButton("Sí, salir") { _, _ ->
                    // Redirige al Login borrando el historial de pantallas anteriores
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // ── 4. AJUSTES: Eliminar cuenta (Acción destructiva en Rojo) ──
        findViewById<LinearLayout>(R.id.btnEliminarCuenta).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("⚠️ ¡Advertencia importante!")
                .setMessage("¿Estás completamente seguro de eliminar tu cuenta? Esta acción no se puede deshacer y perderás todo tu historial de entrenamientos.")
                .setPositiveButton("Eliminar definitivamente") { _, _ ->
                    // En lugar de eliminar, lanzamos el segundo diálogo pidiendo la contraseña
                    mostrarDialogoConfirmacionPassword()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // ── 5. ACERCA DE: Términos y condiciones (Ventana emergente rápida) ──
        findViewById<LinearLayout>(R.id.btnTerminos).setOnClickListener {
            startActivity(Intent(this, TerminosActivity::class.java))
        }

        // ── 6. ACERCA DE: Contacto y soporte ──
        findViewById<LinearLayout>(R.id.btnContacto).setOnClickListener {
            startActivity(Intent(this, ContactoActivity::class.java))
        }

        // ── 7. VALORAR LA APP (Con tus mensajes originales y bloqueo de voto único) ──
        val myRatingBar   = findViewById<RatingBar>(R.id.myRatingBar)
        val tvRateResult = findViewById<TextView>(R.id.tvRateResult)
        val btnEnviar    = findViewById<Button>(R.id.btnEnviarValoracion)

        btnEnviar.setOnClickListener {
            val calificacion = myRatingBar.rating

            if (calificacion == 0f) {
                Toast.makeText(this, "Selecciona al menos una estrella", Toast.LENGTH_SHORT).show()
            } else {
                val mensaje = when (calificacion) {
                    1f -> "⭐ Gracias por tu opinión. ¡Trabajaremos para mejorar!"
                    2f -> "⭐⭐ Gracias, tomaremos en cuenta tus comentarios."
                    3f -> "⭐⭐⭐ ¡Gracias! Seguiremos mejorando."
                    4f -> "⭐⭐⭐⭐ ¡Nos alegra que te guste EntrenaIPN!"
                    5f -> "⭐⭐⭐⭐⭐ ¡Muchas gracias! Tu apoyo nos motiva."
                    else -> "¡Gracias por tu calificación!"
                }
                tvRateResult.text = mensaje
                Toast.makeText(this, "¡Valoración enviada!", Toast.LENGTH_SHORT).show()

                // 🌟 LÓGICA DE VOTO ÚNICO (Añadido aquí):
                btnEnviar.isEnabled = false       // Desactiva el botón por completo
                btnEnviar.alpha = 0.5f             // Lo vuelve translúcido para indicar visualmente que está apagado
                myRatingBar.setIsIndicator(true)   // Congela el RatingBar para que ya no se puedan mover las estrellas
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val tvUsuario = findViewById<TextView>(R.id.tvConfigUsuario)
        val tvEmail   = findViewById<TextView>(R.id.tvConfigEmail)

        val usuarioGuardado = UserPreferences.getUsuario(this)
        val emailGuardado   = UserPreferences.getEmail(this)

        if (usuarioGuardado.isNotEmpty()) tvUsuario.text = usuarioGuardado
        if (emailGuardado.isNotEmpty())   tvEmail.text   = emailGuardado

        val ruta = UserPreferences.getFotoPerfil(this)
        if (ruta.isNotEmpty()) {
            val archivo = File(ruta)
            if (archivo.exists()) {
                val imgConfigPerfil = findViewById<ImageView>(R.id.imgConfigPerfil)
                imgConfigPerfil.setImageBitmap(BitmapFactory.decodeFile(ruta))
                imgConfigPerfil.clearColorFilter()
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