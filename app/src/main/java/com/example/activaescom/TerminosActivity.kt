package com.example.activaescom

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TerminosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Vinculamos esta lógica con el diseño XML de los términos
        setContentView(R.layout.activity_terminos)

        // 2. Buscamos el botón "Aceptar" por su ID
        val btnAceptar = findViewById<Button>(R.id.btn_aceptar_terminos)

        // 3. Configuramos la acción al hacer clic
        btnAceptar.setOnClickListener {
            finish() // Cierra esta pantalla y te regresa a la Configuración
        }
    }
}