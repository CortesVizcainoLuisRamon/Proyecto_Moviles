package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etUsuario         = findViewById<EditText>(R.id.etUsuario)
        val etBoleta          = findViewById<EditText>(R.id.etBoleta)
        val etEmail           = findViewById<EditText>(R.id.etEmail)
        val etPassword        = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)

        // Tab "Iniciar sesión" → regresa al Login
        findViewById<Button>(R.id.btnTabLogin).setOnClickListener {
            finish()
        }

        // Texto "¿Ya tienes cuenta? Inicia sesión" → igual
        findViewById<TextView>(R.id.tvYaTienesCuenta).setOnClickListener {
            finish()
        }

        // Botón Crear cuenta
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {

            val usuario  = etUsuario.text.toString().trim()
            val boleta   = etBoleta.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm  = etPasswordConfirm.text.toString()

            // ── Validaciones ──────────────────────────────────────────

            if (usuario.isEmpty()) {
                etUsuario.error = "El usuario es obligatorio"
                etUsuario.requestFocus()
                return@setOnClickListener
            }

            if (boleta.isEmpty()) {
                etBoleta.error = "La boleta es obligatoria"
                etBoleta.requestFocus()
                return@setOnClickListener
            }
            if (boleta.length != 10) {
                etBoleta.error = "La boleta debe tener 10 dígitos"
                etBoleta.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "El correo es obligatorio"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Ingresa un correo válido"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "La contraseña es obligatoria"
                etPassword.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = "La contraseña debe tener al menos 6 caracteres"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (confirm.isEmpty()) {
                etPasswordConfirm.error = "Confirma tu contraseña"
                etPasswordConfirm.requestFocus()
                return@setOnClickListener
            }
            if (password != confirm) {
                etPasswordConfirm.error = "Las contraseñas no coinciden"
                etPasswordConfirm.requestFocus()
                return@setOnClickListener
            }

            // ── Todo correcto ─────────────────────────────────────────

            // ✅ Guardar datos para el perfil
            UserPreferences.guardarDatosRegistro(this, usuario, boleta, email)

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("emailRegistrado", email)
            intent.putExtra("passwordRegistrado", password)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}