package com.example.activaescom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Entrar → va a MainActivity
        findViewById<Button>(R.id.btnEntrar).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Tab Registrarse → va a RegistroActivity
        findViewById<Button>(R.id.btnTabRegister).setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}
