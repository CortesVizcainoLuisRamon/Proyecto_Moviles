package com.example.activaescom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout

class FuerzaActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_fuerza)

        // Inicializar el DrawerLayout para el menú lateral
        drawerLayout = findViewById(R.id.drawerLayout)

        // Conectar la barra de navegación inferior y el botón hamburguesa
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // TODO: Aquí puedes añadir los listeners para registrar las series (btnRegistrarSerie),
        // obtener los valores de los EditText (etSeries, etReps, etPeso) e interactuar con
        // los chips de los grupos musculares (btnPecho, btnEspalda, etc.).
    }

    override fun onBackPressed() {
        // Si el menú lateral está abierto, lo cerramos primero antes de salir de la pantalla
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}