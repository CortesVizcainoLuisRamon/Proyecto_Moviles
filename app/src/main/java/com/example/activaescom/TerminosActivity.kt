package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class TerminosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminos)

        findViewById<android.widget.ImageButton>(R.id.navConfig).isSelected = true

        val btnAceptar = findViewById<Button>(R.id.btn_aceptar_terminos)
        btnAceptar.setOnClickListener { finish() }

        // --- NAVEGACIÓN BARRA INFERIOR ---
        findViewById<ImageButton>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        findViewById<ImageButton>(R.id.navPerfil).setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
            finish()
        }

        findViewById<ImageButton>(R.id.navConfig).setOnClickListener {
            finish() // Regresa a ConfigActivity
        }
    }
}