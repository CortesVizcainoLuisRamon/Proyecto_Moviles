package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.FuerzaConfiguracionEntity
import com.example.activaescom.database.entities.FuerzaDetalleEntity
import com.example.activaescom.database.repository.FuerzaRepository

class FuerzaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository:
            FuerzaRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .fuerzaDao()

        repository =
            FuerzaRepository(dao)
    }

    suspend fun insertarConfiguracion(
        configuracion:
        FuerzaConfiguracionEntity
    ) {

        repository
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        FuerzaDetalleEntity
    ) {

        repository
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerFuerzaCompleta(
        usuarioId: Int
    ) =
        repository.obtenerFuerzaCompleta(
            usuarioId
        )
}