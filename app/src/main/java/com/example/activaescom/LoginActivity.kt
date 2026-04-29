package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private var emailGuardado    = ""
    private var passwordGuardada = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail    = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        // ✅ Cargar email guardado en SharedPreferences (si ya se registró antes)
        val emailPrefs = UserPreferences.getEmail(this)
        if (emailPrefs.isNotEmpty()) {
            emailGuardado = emailPrefs
        }

        // Si venimos del Registro, precargamos el email y guardamos los datos
        intent.getStringExtra("emailRegistrado")?.let { emailReg ->
            emailGuardado    = emailReg
            passwordGuardada = intent.getStringExtra("passwordRegistrado") ?: ""
            etEmail.setText(emailReg)
        }

        // Botón ENTRAR
        findViewById<Button>(R.id.btnEntrar).setOnClickListener {

            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty()) {
                etEmail.error = "Ingresa tu correo"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Ingresa tu contraseña"
                etPassword.requestFocus()
                return@setOnClickListener
            }

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