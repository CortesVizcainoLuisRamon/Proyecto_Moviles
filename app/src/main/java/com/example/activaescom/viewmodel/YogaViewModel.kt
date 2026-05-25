package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.YogaConfiguracionEntity
import com.example.activaescom.database.entities.YogaDetalleEntity
import com.example.activaescom.database.repository.YogaRepository

class YogaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository:
            YogaRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .yogaDao()

        repository =
            YogaRepository(dao)
    }

    suspend fun insertarConfiguracion(
        configuracion:
        YogaConfiguracionEntity
    ) {

        repository
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        YogaDetalleEntity
    ) {

        repository
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerYogaCompleto(
        usuarioId: Int
    ) =
        repository.obtenerYogaCompleto(
            usuarioId
        )
}