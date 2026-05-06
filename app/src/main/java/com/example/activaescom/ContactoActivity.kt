package com.example.activaescom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class ContactoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacto)

        val btnRamon = findViewById<LinearLayout>(R.id.btn_correo_ramon)
        val btnDavid = findViewById<LinearLayout>(R.id.btn_correo_david)
        val btnVolver = findViewById<Button>(R.id.btn_volver_contacto)

        // Acción para enviar correo a Ramón
        btnRamon.setOnClickListener {
            abrirAppDeCorreo("lcortesv1900@alumno.ipn.mx")
        }

        // Acción para enviar correo a David
        btnDavid.setOnClickListener {
            abrirAppDeCorreo("david78@alumno.ipn.mx")
        }

        // Acción para el botón de regresar
        btnVolver.setOnClickListener {
            finish()
        }
    }

    // Esta función centraliza la lógica para no repetir código
    private fun abrirAppDeCorreo(correoDestino: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            // El esquema "mailto:" le dice a Android que busque apps de correo
            data = Uri.parse("mailto:$correoDestino")
            // Agregamos un asunto por defecto para que sea más profesional
            putExtra(Intent.EXTRA_SUBJECT, "Soporte app EntrenaIPN")
        }
        startActivity(intent)
    }
}