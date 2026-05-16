package com.example.activaescom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout

class BicicletaActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContentView(R.layout.activity_bicicleta)

        // Inicializar el DrawerLayout para el menú lateral
        drawerLayout = findViewById(R.id.drawerLayout)

        // Conectar la barra de navegación inferior y el botón hamburguesa
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // TODO: Aquí puedes inicializar tus controladores para el cronómetro (tvTimer),
        // los botones (btnPausar, btnFinalizar) y capturar el terreno seleccionado.
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