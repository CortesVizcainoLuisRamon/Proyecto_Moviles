package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class PrivacidadActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacidad)

        findViewById<android.widget.ImageButton>(R.id.navConfig).isSelected = true

        // 1. Botón "Entendido" que ya tenías
        val btnEntendido = findViewById<Button>(R.id.btn_entendido)
        btnEntendido.setOnClickListener {
            finish()
        }

        // 2. Lógica de la barra inferior (Bottom Navigation)

        // Botón Inicio
        findViewById<ImageButton>(R.id.navInicio).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Botón Perfil
        findViewById<ImageButton>(R.id.navPerfil).setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Botón Configuración
        findViewById<ImageButton>(R.id.navConfig).setOnClickListener {
            // Como ya estás en la ruta de configuración, simplemente cerramos Privacidad
            finish()
        }
    }
}