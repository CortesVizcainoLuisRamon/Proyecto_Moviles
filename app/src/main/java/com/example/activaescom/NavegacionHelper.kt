package com.example.activaescom

import android.app.Activity
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

object NavegacionHelper {

    fun configurarNavegacion(activity: Activity, drawerLayout: DrawerLayout) {

        // ── BARRA INFERIOR ──────────────────────────────────────────────────
        activity.findViewById<ImageButton>(R.id.navInicio).setOnClickListener {
            if (activity !is MainActivity) {
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }
        }

        activity.findViewById<ImageButton>(R.id.navPerfil).setOnClickListener {
            if (activity !is PerfilActivity) {
                activity.startActivity(Intent(activity, PerfilActivity::class.java))
                activity.finish()
            }
        }

        activity.findViewById<ImageButton>(R.id.navConfig).setOnClickListener {
            if (activity !is ConfigActivity) {
                activity.startActivity(Intent(activity, ConfigActivity::class.java))
                activity.finish()
            }
        }

        // ── BOTÓN HAMBURGUESA (opcional, no crashea si no existe en el XML) ─
        activity.findViewById<ImageButton?>(R.id.btnHamburguesa)?.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ── MENÚ LATERAL ────────────────────────────────────────────────────
        val navView = activity.findViewById<NavigationView>(R.id.navigationView)
        val headerView = navView.getHeaderView(0)

        headerView.findViewById<TextView>(R.id.menu_actividad).setOnClickListener {
            if (activity !is MainActivity) {
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_progreso).setOnClickListener {
            if (activity !is ProgresoActivity) {
                activity.startActivity(Intent(activity, ProgresoActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_carrera).setOnClickListener {
            if (activity !is CarreraActivity) {
                activity.startActivity(Intent(activity, CarreraActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_nuevo).setOnClickListener {
            if (activity !is NuevoEntrenamientoActivity) {
                activity.startActivity(Intent(activity, NuevoEntrenamientoActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_config).setOnClickListener {
            if (activity !is ConfigActivity) {
                activity.startActivity(Intent(activity, ConfigActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_logout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
    }
}
