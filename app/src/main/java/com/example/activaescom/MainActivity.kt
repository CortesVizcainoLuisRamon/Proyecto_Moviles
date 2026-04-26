package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // "Última Actividad" → CarreraActivity
        findViewById<CardView>(R.id.cardUltimaActividad).setOnClickListener {
            startActivity(Intent(this, CarreraActivity::class.java))
        }

        // "Progreso" card → ProgresoActivity
        findViewById<CardView>(R.id.cardProgreso).setOnClickListener {
            startActivity(Intent(this, ProgresoActivity::class.java))
        }

        // "Rutinas" card → puedes agregar su Activity aquí en el futuro
        findViewById<CardView>(R.id.cardRutinas).setOnClickListener {
            // startActivity(Intent(this, RutinasActivity::class.java))
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