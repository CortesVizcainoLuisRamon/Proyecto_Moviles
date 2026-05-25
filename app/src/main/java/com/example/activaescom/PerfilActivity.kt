package com.example.activaescom

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.io.File
import androidx.lifecycle.lifecycleScope
import com.example.activaescom.database.entities.UsuarioEntity
import com.example.activaescom.viewmodel.UsuarioViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
class PerfilActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var usuarioViewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        usuarioViewModel = UsuarioViewModel(application)
        findViewById<android.widget.ImageButton>(R.id.navPerfil).isSelected = true

        drawerLayout = findViewById(R.id.drawerLayout)
        NavegacionHelper.configurarNavegacion(this, drawerLayout)

        cargarDatos()

        // Botón editar perfil
        findViewById<LinearLayout>(R.id.layoutEditarPerfil).setOnClickListener {
            startActivity(Intent(this, EditarPerfilActivity::class.java))
        }

        // Botón cambiar contraseña (vista pendiente)
        findViewById<TextView>(R.id.btnCambiarPassword).setOnClickListener {
            // startActivity(Intent(this, CambiarPasswordActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {

        lifecycleScope.launch {

            val usuarioId =
                UserPreferences.getUsuarioId(
                    this@PerfilActivity
                )

            val usuario =
                usuarioViewModel
                    .obtenerUsuarioPorId(
                        usuarioId
                    )

            if (usuario == null) {

                findViewById<TextView>(R.id.tvUsuario).text =
                    "ROOM NULL"

                return@launch
            }

            // USUARIO

            findViewById<TextView>(R.id.tvUsuario).text =
                usuario.usuario

            // CORREO

            findViewById<TextView>(R.id.tvCorreo).text =
                usuario.correo

            findViewById<TextView>(R.id.tvBoleta).text =
                usuario.boleta

            // NOMBRE COMPLETO

            findViewById<TextView>(R.id.tvNombreCompleto).text =

                if (
                    usuario.nombre.isNotEmpty() ||
                    usuario.apellido.isNotEmpty()
                ) {

                    "${usuario.nombre} ${usuario.apellido}".trim()

                } else {

                    "Completa tu perfil"
                }

            // NOMBRE

            findViewById<TextView>(R.id.tvNombre).text =

                if (usuario.nombre.isNotEmpty())
                    usuario.nombre
                else
                    "Sin registrar"

            // APELLIDO

            findViewById<TextView>(R.id.tvApellido).text =

                if (usuario.apellido.isNotEmpty())
                    usuario.apellido
                else
                    "Sin registrar"

            // FECHA

            findViewById<TextView>(R.id.tvFechaNacimiento).text =

                if (usuario.fechaNacimiento.isNotEmpty())
                    usuario.fechaNacimiento
                else
                    "Sin registrar"

            // FOTO

            val ruta =
                UserPreferences.getFotoPerfil(
                    this@PerfilActivity
                )

            if (ruta.isNotEmpty()) {

                val archivo = File(ruta)

                if (archivo.exists()) {

                    val imgPerfil =
                        findViewById<ImageView>(
                            R.id.imgPerfil
                        )

                    imgPerfil.setImageBitmap(
                        BitmapFactory.decodeFile(ruta)
                    )

                    imgPerfil.clearColorFilter()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }
}