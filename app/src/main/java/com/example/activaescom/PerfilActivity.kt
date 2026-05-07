package com.example.activaescom

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File

class PerfilActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        cargarDatos()

        // Botón editar perfil
        findViewById<LinearLayout>(R.id.layoutEditarPerfil).setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        // Botón cambiar contraseña (vista pendiente)
        findViewById<Button>(R.id.btnCambiarPassword).setOnClickListener {
            // startActivity(Intent(this, CambiarPasswordActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {
        val nombre   = UserPreferences.getNombre(this)
        val apellido = UserPreferences.getApellido(this)

        findViewById<TextView>(R.id.tvNombreCompleto).text =
            if (nombre.isNotEmpty() || apellido.isNotEmpty()) "$nombre $apellido".trim()
            else ""

        findViewById<TextView>(R.id.tvUsuario).text         = UserPreferences.getUsuario(this)
        findViewById<TextView>(R.id.tvBoleta).text          = UserPreferences.getBoleta(this)
        findViewById<TextView>(R.id.tvCorreo).text          = UserPreferences.getEmail(this)
        findViewById<TextView>(R.id.tvNombre).text          = nombre
        findViewById<TextView>(R.id.tvApellido).text        = apellido
        findViewById<TextView>(R.id.tvFechaNacimiento).text = UserPreferences.getFecha(this)

        // ── Cargar foto de perfil ──────────────────────────────────────────
        val ruta = UserPreferences.getFotoPerfil(this)
        if (ruta.isNotEmpty()) {
            val archivo = File(ruta)
            if (archivo.exists()) {
                val imgPerfil = findViewById<ImageView>(R.id.imgPerfil)
                imgPerfil.setImageBitmap(BitmapFactory.decodeFile(ruta))
                imgPerfil.clearColorFilter() // quita el tint blanco del XML
            }
        }
        // ──────────────────────────────────────────────────────────────────
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}