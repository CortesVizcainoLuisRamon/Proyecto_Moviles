package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class ConfigActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // ── 1. Datos del usuario ──
        val tvUsuario = findViewById<TextView>(R.id.tvConfigUsuario)
        val tvEmail   = findViewById<TextView>(R.id.tvConfigEmail)

        val usuarioGuardado = UserPreferences.getUsuario(this)
        val emailGuardado   = UserPreferences.getEmail(this)

        if (usuarioGuardado.isNotEmpty()) tvUsuario.text = usuarioGuardado
        if (emailGuardado.isNotEmpty())   tvEmail.text   = emailGuardado

        // ── 2. Ir a Perfil ──
        findViewById<LinearLayout>(R.id.btnConfigPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }

        // ── 3. Cerrar sesión ──
        findViewById<LinearLayout>(R.id.btnCerrarSesion).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // ── 4. Privacidad de datos ──
        findViewById<LinearLayout>(R.id.btnPrivacidad).setOnClickListener {
            startActivity(Intent(this, PrivacidadActivity::class.java))
        }

        // ── 5. Eliminar cuenta ──
        findViewById<LinearLayout>(R.id.btnEliminarCuenta).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar cuenta")
            builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta de EntrenaIPN? Esta acción es permanente y perderás todo tu historial de entrenamiento.")
            builder.setPositiveButton("Sí, eliminar") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            val dialog = builder.create()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }

        // ── 6. Términos y condiciones ──
        findViewById<LinearLayout>(R.id.btnTerminos).setOnClickListener {
            startActivity(Intent(this, TerminosActivity::class.java))
        }

        // ── 7. Contacto y soporte ──
        findViewById<LinearLayout>(R.id.btnContacto).setOnClickListener {
            startActivity(Intent(this, ContactoActivity::class.java))
        }

        // ── 8. RatingBar - Valorar la app ──
        val myRatingBar  = findViewById<RatingBar>(R.id.myRatingBar)
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