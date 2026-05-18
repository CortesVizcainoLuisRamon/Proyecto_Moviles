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

class ConfigActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // Cargamos los datos del usuario logueado
        cargarDatosUsuario()

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
                    Toast.makeText(this, "Cuenta eliminada con éxito", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
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