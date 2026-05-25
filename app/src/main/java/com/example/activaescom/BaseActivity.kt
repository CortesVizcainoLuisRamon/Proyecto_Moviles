package com.example.activaescom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Esto apaga el modo noche a nivel de código para toda la app
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)

        // Le decimos a la ventana que respete el espacio de la barra de estado (para que no se suba el contenido)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // 👇 COMENTA O BORRA ESTAS DOS LÍNEAS 👇
        // window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        // WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
    }
}