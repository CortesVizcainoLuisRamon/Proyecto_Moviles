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
    private const val KEY_FOTO      = "foto_perfil"   // ← nuevo

    private const val KEY_USUARIO_ID = "usuario_id"

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

    fun guardarFotoPerfil(context: Context, ruta: String) {   // ← nuevo
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_FOTO, ruta)
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
    fun getFotoPerfil(context: Context) = pref(context).getString(KEY_FOTO, "") ?: ""  // ← nuevo

    private fun pref(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun guardarEmail(
        context: Context,
        email: String
    ) {

        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putString("email_actual", email)
            .apply()
    }

    fun getEmailActual(
        context: Context
    ): String {

        return context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .getString("email_actual", "") ?: ""
    }

    fun guardarUsuarioId(
        context: Context,
        id: Int
    ) {

        pref(context)
            .edit()
            .putInt("usuario_id", id)
            .apply()
    }

    fun getUsuarioId(
        context: Context
    ): Int {

        return pref(context)
            .getInt("usuario_id", -1)
    }

    fun saveRecordatorioActivo(

        context: Context,

        activo: Boolean

    ) {

        val prefs =

            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putBoolean(
                "recordatorio_activo",
                activo
            )
            .apply()
    }

    fun getRecordatorioActivo(
        context: Context
    ): Boolean {

        val prefs =

            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return prefs.getBoolean(
            "recordatorio_activo",
            false
        )
    }

    fun saveHoraRecordatorio(

        context: Context,

        hora: String

    ) {

        val prefs =

            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        prefs.edit()
            .putString(
                "hora_recordatorio",
                hora
            )
            .apply()
    }

    fun getHoraRecordatorio(
        context: Context
    ): String {

        val prefs =

            context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        return prefs.getString(
            "hora_recordatorio",
            "7:00 PM"
        ) ?: "7:00 PM"
    }

    fun guardarValoracionRealizada(

        context: Context,

        realizada: Boolean

    ) {

        pref(context)
            .edit()
            .putBoolean(
                "valoracion_realizada",
                realizada
            )
            .apply()
    }

    fun getValoracionRealizada(
        context: Context
    ): Boolean {

        return pref(context)
            .getBoolean(
                "valoracion_realizada",
                false
            )
    }

    fun cerrarSesion(
        context: Context
    ) {

        pref(context)
            .edit()

            .remove(KEY_USUARIO)
            .remove(KEY_EMAIL)
            .remove(KEY_USUARIO_ID)

            .apply()
    }

}