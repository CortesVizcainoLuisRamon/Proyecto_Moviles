package com.example.activaescom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout

class ContactoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacto)

        findViewById<android.widget.ImageButton>(R.id.navConfig).isSelected = true

        val btnRamon = findViewById<LinearLayout>(R.id.btn_correo_ramon)
        val btnDavid = findViewById<LinearLayout>(R.id.btn_correo_david)
        val btnVolver = findViewById<Button>(R.id.btn_volver_contacto)

        // Acción para enviar correo a Ramón
        btnRamon.setOnClickListener { abrirAppDeCorreo("lcortesv1900@alumno.ipn.mx") }
        // Acción para enviar correo a David
        btnDavid.setOnClickListener { abrirAppDeCorreo("david78@alumno.ipn.mx") }

        // Acción para abrir Maps
        findViewById<LinearLayout>(R.id.btn_abrir_maps).setOnClickListener {
            val uri = Uri.parse("geo:19.5046,-99.1468?q=ESCOM+IPN+Av.+Juan+de+Dios+Bátiz,+Ciudad+de+México")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
            if (intent.resolveActivity(packageManager) != null) startActivity(intent)
            else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/")))
        }

        btnVolver.setOnClickListener { finish() }

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

    private fun abrirAppDeCorreo(correoDestino: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$correoDestino")
            putExtra(Intent.EXTRA_SUBJECT, "Soporte app EntrenaIPN")
        }
        startActivity(intent)
    }
}