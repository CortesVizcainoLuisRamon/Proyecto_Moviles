package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    // Simulamos la "base de datos" en memoria con estas variables
    private var emailGuardado    = ""
    private var passwordGuardada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias a los campos
        val etEmail    = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // Si venimos del Registro, precargamos el email y guardamos los datos
        intent.getStringExtra("emailRegistrado")?.let { emailReg ->
            emailGuardado    = emailReg
            passwordGuardada = intent.getStringExtra("passwordRegistrado") ?: ""
            // Precargamos el email para que el usuario no lo escriba de nuevo
            etEmail.setText(emailReg)
        }

        // Botón ENTRAR → valida y navega al MainActivity
        findViewById<Button>(R.id.btnEntrar).setOnClickListener {

            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            // ── Validaciones ──────────────────────────────────────────

            // 1. Email obligatorio
            if (email.isEmpty()) {
                etEmail.error = "Ingresa tu correo"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            // 2. Contraseña obligatoria
            if (password.isEmpty()) {
                etPassword.error = "Ingresa tu contraseña"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // 3. Verificar que coincidan con los datos registrados
            if (email != emailGuardado || password != passwordGuardada) {
                etPassword.error = "Correo o contraseña incorrectos"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            // ── Todo correcto: ir a MainActivity ──
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Tab Registrarse → va a RegistroActivity
        findViewById<Button>(R.id.btnTabRegister).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}