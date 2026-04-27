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

        // Referencias a los campos
        val etNombre          = findViewById<EditText>(R.id.etNombre)
        val etBoleta          = findViewById<EditText>(R.id.etBoleta)
        val etEmail           = findViewById<EditText>(R.id.etEmail)
        val etPassword        = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)

        // Tab "Iniciar sesión" → regresa al Login
        val btnTabLogin = findViewById<Button>(R.id.btnTabLogin)
        btnTabLogin.setOnClickListener {
            finish()
        }

        // Texto "¿Ya tienes cuenta? Inicia sesión" → igual
        val tvYaTienesCuenta = findViewById<TextView>(R.id.tvYaTienesCuenta)
        tvYaTienesCuenta.setOnClickListener {
            finish()
        }

        // Botón Crear cuenta
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {

            val nombre   = etNombre.text.toString().trim()
            val boleta   = etBoleta.text.toString().trim()
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm  = etPasswordConfirm.text.toString()

            // ── Validaciones ──────────────────────────────────────────

            // 1. Nombre obligatorio
            if (nombre.isEmpty()) {
                etNombre.error = "El nombre es obligatorio"
                etNombre.requestFocus()
                return@setOnClickListener
            }

            // 2. Boleta obligatoria y debe tener 10 dígitos
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

            // 3. Email obligatorio y con formato válido
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

            // 4. Contraseña obligatoria y mínimo 6 caracteres
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

            // 5. Confirmar contraseña obligatoria y debe coincidir
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

            // ── Todo correcto: pasar datos al Login para simular sesión ──
            // Guardamos los datos en un Intent de vuelta al Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("emailRegistrado", email)
            intent.putExtra("passwordRegistrado", password)
            // Evita que el usuario regrese al registro con el botón atrás
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }
}