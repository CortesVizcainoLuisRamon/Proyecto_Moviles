package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.BicicletaConfiguracionEntity
import com.example.activaescom.database.entities.BicicletaDetalleEntity
import com.example.activaescom.database.repository.BicicletaRepository

class BicicletaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository:
            BicicletaRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .bicicletaDao()

        repository =
            BicicletaRepository(dao)
    }

    suspend fun insertarConfiguracion(
        configuracion:
        BicicletaConfiguracionEntity
    ) {

        repository
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        BicicletaDetalleEntity
    ) {

        repository
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerBicicletaCompleta(
        usuarioId: Int
    ) =
        repository.obtenerBicicletaCompleta(
            usuarioId
        )
}