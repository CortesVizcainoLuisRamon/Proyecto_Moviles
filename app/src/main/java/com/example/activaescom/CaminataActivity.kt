package com.example.activaescom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout

class CaminataActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_caminata)

        // Inicializar el DrawerLayout para el menú lateral
        drawerLayout = findViewById(R.id.drawerLayout)

        // Conectar la barra de navegación inferior y el botón hamburguesa
        NavegacionHelper.configurarNavegacion(this, drawerLayout)
    }

    override fun onBackPressed() {
        // Cerramos el menú lateral si está abierto
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}