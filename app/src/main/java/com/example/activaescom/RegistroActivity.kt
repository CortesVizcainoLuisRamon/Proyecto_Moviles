package com.example.activaescom

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Tab "Iniciar sesión" → regresa al Login
        val btnTabLogin = findViewById<Button>(R.id.btnTabLogin)
        btnTabLogin.setOnClickListener {
            finish()
        }

        // Texto "¿Ya tienes cuenta? Inicia sesión" → igual
        val tvYaTienesCuenta = findViewById<TextView>(R.id.tvYaTienesCuenta)
        tvYaTienesCuenta.setOnClickListener {
            finish()
        }

        // Botón Crear cuenta
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {
            // Aquí va tu lógica de registro
        }
    }
}