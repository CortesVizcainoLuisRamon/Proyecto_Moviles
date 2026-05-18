package com.example.activaescom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.button.MaterialButton

class BicicletaActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicicleta)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // 1. VINCULAR ELEMENTOS DEL XML
        val layoutControlesActivos = findViewById<LinearLayout>(R.id.layoutControlesActivos)
        val btnIniciarCronometro   = findViewById<MaterialButton>(R.id.btnIniciarCronometro)

        val tvConfigRutinaTitulo   = findViewById<TextView>(R.id.tvConfigRutinaTitulo)
        val tvConfigMeta           = findViewById<TextView>(R.id.tvConfigMeta)
        val tvConfigSueno          = findViewById<TextView>(R.id.tvConfigSueno)
        val tvConfigLugar          = findViewById<TextView>(R.id.tvConfigLugar)
        val tvConfigDuracion       = findViewById<TextView>(R.id.tvConfigDuracion)
        val tvConfigFecha          = findViewById<TextView>(R.id.guardarNotas)
        val tvConfigNotas          = findViewById<TextView>(R.id.tvConfigNotas)
        val layoutConfigNotas      = findViewById<LinearLayout>(R.id.layoutConfigNotas)

        val btnPlataformaSpotify   = findViewById<LinearLayout>(R.id.btnPlataformaSpotify)
        val btnPlataformaYoutube   = findViewById<LinearLayout>(R.id.btnPlataformaYoutube)
        val btnPlataformaApple     = findViewById<LinearLayout>(R.id.btnPlataformaApple)
        val btnPlataformaAmazon    = findViewById<LinearLayout>(R.id.btnPlataformaAmazon)
        val btnPlataformaDeezer    = findViewById<LinearLayout>(R.id.btnPlataformaDeezer)

        // 2. EXTRAER DATOS DEL INTENT
        val rutinaNombre       = intent.getStringExtra("NOMBRE_RUTINA")        ?: "Bicicleta Individual"
        val metaDistancia      = intent.getStringExtra("META_DISTANCIA")       ?: "20.0 km"
        val horasSueno         = intent.getIntExtra("HORAS_SUENO", 7)
        val lugarSeleccionado  = intent.getStringExtra("LUGAR_ENTRENAMIENTO")  ?: "Exterior"
        val duracionPactada    = intent.getIntExtra("DURACION_ESTIMADA", 30)
        val notasPrevias       = intent.getStringExtra("NOTAS")                ?: ""
        val fechaEntrenamiento = intent.getStringExtra("FECHA_ENTRENAMIENTO")  ?: "No especificada"

        // 3. PINTAR LOS DATOS EN LAS VISTAS
        tvConfigRutinaTitulo.text = rutinaNombre
        tvConfigMeta.text         = "Meta: $metaDistancia"
        tvConfigSueno.text        = "💤 : $horasSueno hrs"
        tvConfigLugar.text        = "📍 : $lugarSeleccionado"
        tvConfigDuracion.text     = "⏱️ : $duracionPactada min"
        tvConfigFecha.text        = fechaEntrenamiento

        if (notasPrevias.isNotEmpty()) {
            tvConfigNotas.text         = "Notas previas: $notasPrevias"
            layoutConfigNotas.visibility = View.VISIBLE
        } else {
            layoutConfigNotas.visibility = View.GONE
        }

        // 4. MÚSICA — redirecciones a plataformas
        btnPlataformaSpotify.setOnClickListener {
            abrirPlataforma("https://open.spotify.com", "com.spotify.music")
        }
        btnPlataformaYoutube.setOnClickListener {
            abrirPlataforma("https://music.youtube.com", "com.google.android.apps.youtube.music")
        }
        btnPlataformaApple.setOnClickListener {
            abrirPlataforma("https://music.apple.com", "com.apple.android.music")
        }
        btnPlataformaAmazon.setOnClickListener {
            abrirPlataforma("https://music.amazon.com", "com.amazon.mp3")
        }
        btnPlataformaDeezer.setOnClickListener {
            abrirPlataforma("https://www.deezer.com", "deezer.android.app")
        }

        // 5. CONTROL DEL CRONÓMETRO
        btnIniciarCronometro.setOnClickListener {
            btnIniciarCronometro.visibility    = View.GONE
            layoutControlesActivos.visibility  = View.VISIBLE
        }
    }

    private fun abrirPlataforma(urlWeb: String, packageName: String) {
        val intentApp = packageManager.getLaunchIntentForPackage(packageName)
        if (intentApp != null) {
            startActivity(intentApp)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb)))
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