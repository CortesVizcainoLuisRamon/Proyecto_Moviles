package com.example.activaescom

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PrivacidadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Vinculamos esta lógica con el diseño XML que creaste
        setContentView(R.layout.activity_privacidad)

        // 2. Buscamos el botón "Entendido" en el diseño
        val btnEntendido = findViewById<Button>(R.id.btn_entendido)

        // 3. Le decimos qué hacer al hacer clic
        btnEntendido.setOnClickListener {
            finish() // Cierra esta pantalla y regresa a Configuración
        }
    }
}