package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.CaminataConfiguracionEntity
import com.example.activaescom.database.entities.CaminataDetalleEntity
import com.example.activaescom.database.repository.CaminataRepository

class CaminataViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository:
            CaminataRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .caminataDao()

        repository =
            CaminataRepository(dao)
    }

    suspend fun insertarConfiguracion(
        configuracion:
        CaminataConfiguracionEntity
    ) {

        repository
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        CaminataDetalleEntity
    ) {

        repository
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerCaminataCompleta(
        usuarioId: Int
    ) =
        repository.obtenerCaminataCompleta(
            usuarioId
        )
}