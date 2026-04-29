package com.example.activaescom

import android.content.Context

object UserPreferences {

    private const val PREFS_NAME = "user_prefs"

    private const val KEY_USUARIO   = "usuario"
    private const val KEY_BOLETA    = "boleta"
    private const val KEY_EMAIL     = "email"
    private const val KEY_NOMBRE    = "nombre"
    private const val KEY_APELLIDO  = "apellido"
    private const val KEY_FECHA     = "fecha_nacimiento"

    // ── GUARDAR ─────────────────────────────────────────────────────────────

    fun guardarDatosRegistro(
        context: Context,
        usuario: String,
        boleta: String,
        email: String
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_USUARIO, usuario)
            putString(KEY_BOLETA, boleta)
            putString(KEY_EMAIL, email)
            apply()
        }
    }

    fun guardarDatosPerfil(
        context: Context,
        usuario: String,
        nombre: String,
        apellido: String,
        fecha: String
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_USUARIO, usuario)
            putString(KEY_NOMBRE, nombre)
            putString(KEY_APELLIDO, apellido)
            putString(KEY_FECHA, fecha)
            apply()
        }
    }

    // ── LEER ─────────────────────────────────────────────────────────────────

    fun getUsuario(context: Context)  = pref(context).getString(KEY_USUARIO, "") ?: ""
    fun getBoleta(context: Context)   = pref(context).getString(KEY_BOLETA, "")  ?: ""
    fun getEmail(context: Context)    = pref(context).getString(KEY_EMAIL, "")   ?: ""
    fun getNombre(context: Context)   = pref(context).getString(KEY_NOMBRE, "")  ?: ""
    fun getApellido(context: Context) = pref(context).getString(KEY_APELLIDO, "") ?: ""
    fun getFecha(context: Context)    = pref(context).getString(KEY_FECHA, "")   ?: ""

    private fun pref(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}