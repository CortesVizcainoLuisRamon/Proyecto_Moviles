package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AlertDialog

class ConfigActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        // ── 1. Referenciar las vistas del XML ──
        val tvUsuario = findViewById<TextView>(R.id.tvConfigUsuario)
        val tvEmail = findViewById<TextView>(R.id.tvConfigEmail)

        // ── 2. Obtener los datos guardados en SharedPreferences ──
        val usuarioGuardado = UserPreferences.getUsuario(this)
        val emailGuardado = UserPreferences.getEmail(this)

        // ── 3. Asignar los datos a la vista (si existen) ──
        if (usuarioGuardado.isNotEmpty()) {
            tvUsuario.text = usuarioGuardado
        }
        if (emailGuardado.isNotEmpty()) {
            tvEmail.text = emailGuardado
        }

        // ── 4. Configurar el clic para ir a Perfil ──
        val btnPerfil = findViewById<LinearLayout>(R.id.btnConfigPerfil)

        btnPerfil.setOnClickListener {
            // Aquí usamos Intent para cambiar a la pantalla de Perfil
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        // ── 5. Configurar el clic para Cerrar Sesión ──
        val btnCerrarSesion = findViewById<LinearLayout>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            // Opcional: Aquí podrías borrar los datos de sesión si usas un flag de "logueado"
            // Por ejemplo: UserPreferences.borrarSesion(this)

            // Redirigir al Login
            val intent = Intent(this, LoginActivity::class.java)

            // Estas "flags" borran todo el historial de pantallas abiertas.
            // Así, si el usuario presiona el botón "Atrás", la app se cierra en lugar de regresar a Configuración.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
            finish() // Cierra la pantalla actual
        }

        // ── Configurar el clic para Privacidad de Datos ──
        val btnPrivacidad = findViewById<LinearLayout>(R.id.btnPrivacidad)

        btnPrivacidad.setOnClickListener {
            // Reemplaza "PrivacidadActivity" si le pusiste otro nombre a la clase Kotlin
            val intent = Intent(this, PrivacidadActivity::class.java)
            startActivity(intent)
        }

        // ── 6. Configurar el clic para Eliminar Cuenta (con Diálogo de confirmación) ──
        val btnEliminarCuenta = findViewById<LinearLayout>(R.id.btnEliminarCuenta)

        btnEliminarCuenta.setOnClickListener {
            // Construimos el cuadro de diálogo
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Eliminar cuenta")
            builder.setMessage("¿Estás seguro de que deseas eliminar tu cuenta de EntrenaIPN? Esta acción es permanente y perderás todo tu historial de entrenamiento.")

            // Botón afirmativo (Rojo)
            builder.setPositiveButton("Sí, eliminar") { dialog, which ->
                // Aquí va la lógica real para borrar datos.
                // Por ejemplo, borrar SharedPreferences:
                // UserPreferences.borrarSesion(this)

                // 1. Redirigimos al Login
                val intent = Intent(this, LoginActivity::class.java)

                // 2. Borramos el historial de pantallas para que no pueda volver atrás
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                // 3. Cerramos la pantalla de configuración
                finish()
            }

            // Botón negativo (Gris/Cancelar)
            builder.setNegativeButton("Cancelar") { dialog, which ->
                // Si el usuario se arrepiente, solo cerramos el diálogo y no hacemos nada
                dialog.dismiss()
            }

            // Mostramos el diálogo en pantalla
            val dialog: AlertDialog = builder.create()
            dialog.show()

            // (Opcional) Cambiar el color del botón "Sí, eliminar" a rojo para que se vea peligroso
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
        }

        // ── 7. Configurar el clic para Términos y Condiciones ──
        val btnTerminos = findViewById<LinearLayout>(R.id.btnTerminos)

        btnTerminos.setOnClickListener {
            val intent = Intent(this, TerminosActivity::class.java)
            startActivity(intent)
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