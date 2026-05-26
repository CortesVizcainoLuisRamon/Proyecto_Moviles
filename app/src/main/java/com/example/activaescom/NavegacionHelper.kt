package com.example.activaescom

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.activaescom.LoginActivity
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

        // ── BOTÓN HAMBURGUESA ───────────────────────────────────────────────
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

        // ── NUEVO ENTRENAMIENTO CON DROPDOWN ────────────────────────────────
        val menuNuevoHeader = headerView.findViewById<LinearLayout>(R.id.menu_nuevo_header)
        val menuNuevoSubmenu = headerView.findViewById<LinearLayout>(R.id.menu_nuevo_submenu)
        val menuNuevoArrow = headerView.findViewById<ImageView>(R.id.menu_nuevo_arrow)

        menuNuevoHeader.setOnClickListener {
            val isVisible = menuNuevoSubmenu.visibility == View.VISIBLE
            if (isVisible) {
                menuNuevoSubmenu.visibility = View.GONE
                menuNuevoArrow.animate().rotation(0f).setDuration(200).start()
            } else {
                menuNuevoSubmenu.visibility = View.VISIBLE
                menuNuevoArrow.animate().rotation(180f).setDuration(200).start()
            }
        }

        // ── EJERCICIOS DEL SUBMENÚ ──────────────────────────────────────────

        headerView.findViewById<TextView>(R.id.menu_config_nuevo_entrenamiento).setOnClickListener {
            activity.startActivity(Intent(activity, NuevoEntrenamientoActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_ejercicio_carrera).setOnClickListener {
            activity.startActivity(Intent(activity, CarreraActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_ejercicio_caminata).setOnClickListener {
            activity.startActivity(Intent(activity, CaminataActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_ejercicio_bicicleta).setOnClickListener {
            activity.startActivity(Intent(activity, BicicletaActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_ejercicio_fuerza).setOnClickListener {
            activity.startActivity(Intent(activity, FuerzaActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_ejercicio_yoga).setOnClickListener {
            activity.startActivity(Intent(activity, YogaActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        headerView.findViewById<TextView>(R.id.menu_ejercicio_natacion).setOnClickListener {
            activity.startActivity(Intent(activity, NatacionActivity::class.java))
            activity.finish()
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // ── CONFIGURACIÓN ───────────────────────────────────────────────────
        headerView.findViewById<TextView>(R.id.menu_config).setOnClickListener {
            if (activity !is ConfigActivity) {
                activity.startActivity(Intent(activity, ConfigActivity::class.java))
                activity.finish()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // ── CERRAR SESIÓN ───────────────────────────────────────────────────
        headerView.findViewById<TextView>(
            R.id.menu_logout
        ).setOnClickListener {

            drawerLayout.closeDrawer(
                GravityCompat.START
            )

            UserPreferences.cerrarSesion(
                activity
            )

            val intent =
                Intent(
                    activity,
                    LoginActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            activity.startActivity(intent)

            activity.finish()
        }
    }
}