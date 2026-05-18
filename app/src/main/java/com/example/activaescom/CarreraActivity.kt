package com.example.activaescom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class CarreraActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrera)

        // Inicializar el DrawerLayout para el menú lateral
        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // 1. VINCULAR LOS ELEMENTOS DINÁMICOS DEL XML
        val layoutControlesActivos = findViewById<LinearLayout>(R.id.layoutControlesActivos)
        val btnIniciarCronometro = findViewById<Button>(R.id.btnIniciarCronometro)

        val tvConfigRutinaTitulo = findViewById<TextView>(R.id.tvConfigRutinaTitulo)
        val tvConfigMeta = findViewById<TextView>(R.id.tvConfigMeta)
        val tvConfigSueno = findViewById<TextView>(R.id.tvConfigSueno)
        val tvConfigLugar = findViewById<TextView>(R.id.tvConfigLugar)
        val tvConfigDuracion = findViewById<TextView>(R.id.tvConfigDuracion)
        val tvConfigFecha = findViewById<TextView>(R.id.guardarNotas)
        val tvConfigNotas = findViewById<TextView>(R.id.tvConfigNotas)
        val layoutConfigNotas = findViewById<LinearLayout>(R.id.layoutConfigNotas)

        // Contenedores de clic de plataformas de música
        val btnPlataformaSpotify = findViewById<LinearLayout>(R.id.btnPlataformaSpotify)
        val btnPlataformaYoutube = findViewById<LinearLayout>(R.id.btnPlataformaYoutube)
        val btnPlataformaApple = findViewById<LinearLayout>(R.id.btnPlataformaApple)
        val btnPlataformaAmazon = findViewById<LinearLayout>(R.id.btnPlataformaAmazon)
        val btnPlataformaDeezer = findViewById<LinearLayout>(R.id.btnPlataformaDeezer)


        // 2. EXTRAER LOS DATOS ENVIADOS DESDE SETUP
        val rutinaNombre = intent.getStringExtra("NOMBRE_RUTINA") ?: "Carrera Individual"
        val metaDistancia = intent.getStringExtra("META_DISTANCIA") ?: "5.0 km"
        val horasSueno = intent.getIntExtra("HORAS_SUENO", 7)
        val lugarSeleccionado = intent.getStringExtra("LUGAR_ENTRENAMIENTO") ?: "Pista"
        val duracionPactada = intent.getIntExtra("DURACION_ESTIMADA", 30)
        val notasPrevias = intent.getStringExtra("NOTAS") ?: ""
        val fechaEntrenamiento = intent.getStringExtra("FECHA_ENTRENAMIENTO") ?: "No especificada"


        // 3. PINTAR LOS DATOS EN LAS TARJETAS Y COMPONENTES
        tvConfigRutinaTitulo.text = rutinaNombre
        tvConfigMeta.text = "Meta: $metaDistancia"
        tvConfigSueno.text = "💤 : $horasSueno hrs"
        tvConfigLugar.text = "📍 : $lugarSeleccionado"
        tvConfigDuracion.text = "⏱️ : $duracionPactada min"
        tvConfigFecha.text = fechaEntrenamiento

        if (notasPrevias.isNotEmpty()) {
            tvConfigNotas.text = "Notas previas: $notasPrevias"
            layoutConfigNotas.visibility = View.VISIBLE
        } else {
            layoutConfigNotas.visibility = View.GONE
        }


        // 4. FUNCIONALIDAD DEL APARTADO DE MÚSICA (REDIRECCIONES)
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


        // 5. CONTROL DE ESTADOS DE ENTRADA (BOTÓN PRINCIPAL)
        btnIniciarCronometro.setOnClickListener {
            btnIniciarCronometro.visibility = View.GONE
            layoutControlesActivos.visibility = View.VISIBLE
        }
    }

    /**
     * Intenta lanzar la aplicación nativa instalada en el dispositivo mediante su Package Name.
     * Si el usuario no tiene la app instalada, la redirige a su sitio web oficial mediante el navegador.
     */
    private fun abrirPlataforma(urlWeb: String, packageName: String) {
        val intentApp = packageManager.getLaunchIntentForPackage(packageName)
        if (intentApp != null) {
            startActivity(intentApp)
        } else {
            val intentWeb = Intent(Intent.ACTION_VIEW, Uri.parse(urlWeb))
            startActivity(intentWeb)
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