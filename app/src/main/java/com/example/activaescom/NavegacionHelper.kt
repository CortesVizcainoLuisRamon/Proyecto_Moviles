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

        // ── BARRA INFERIOR (CLICS INTERACTIVOS) ──────────────────────────────
        // Se buscan los IDs asignados a los botones del XML de la barra inferior

        activity.findViewById<ImageButton?>(R.id.navInicio)?.setOnClickListener {
            if (activity !is MainActivity) {
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }
        }

        activity.findViewById<ImageButton?>(R.id.navPerfil)?.setOnClickListener {
            if (activity !is PerfilActivity) {
                activity.startActivity(Intent(activity, PerfilActivity::class.java))
                activity.finish()
            }
        }

        // ✅ CORREGIDO: Ahora redirige correctamente a ConfigActivity en lugar de HistorialActivity
        activity.findViewById<ImageButton?>(R.id.navConfig)?.setOnClickListener {
            if (activity !is ConfigActivity) {
                activity.startActivity(Intent(activity, ConfigActivity::class.java))
                activity.finish()
            }
        }

        // ── BOTÓN HAMBURGUESA ───────────────────────────────────────────────
        activity.findViewById<ImageButton?>(R.id.btnHamburguesa)?.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // ── MENÚ LATERAL (DRAWER) ────────────────────────────────────────────
        val navView = activity.findViewById<NavigationView>(R.id.navigationView)
        val headerView = navView?.getHeaderView(0)

        // Opción: Inicio
        headerView?.findViewById<TextView>(R.id.menu_actividad)?.setOnClickListener {
            if (activity !is MainActivity) {
                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Opción: Nuevo Entrenamiento
        headerView?.findViewById<TextView>(R.id.menu_config_nuevo_entrenamiento)?.setOnClickListener {
            if (activity !is NuevoEntrenamientoActivity) {
                activity.startActivity(Intent(activity, NuevoEntrenamientoActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Opción: Historial de Entrenamientos
        headerView?.findViewById<TextView>(R.id.menu_historial)?.setOnClickListener {
            if (activity !is HistorialActivity) {
                activity.startActivity(Intent(activity, HistorialActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Opción: Cerrar Sesión
        headerView?.findViewById<TextView>(R.id.menu_logout)?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            UserPreferences.cerrarSesion(activity)
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
            activity.finish()
        }
    }
}