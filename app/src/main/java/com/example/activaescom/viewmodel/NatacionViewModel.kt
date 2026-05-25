package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.NatacionConfiguracionEntity
import com.example.activaescom.database.entities.NatacionDetalleEntity
import com.example.activaescom.database.repository.NatacionRepository

class NatacionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository:
            NatacionRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .natacionDao()

        repository =
            NatacionRepository(dao)
    }

    suspend fun insertarConfiguracion(
        configuracion:
        NatacionConfiguracionEntity
    ) {

        repository
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        NatacionDetalleEntity
    ) {

        repository
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerNatacionCompleta(
        usuarioId: Int
    ) =
        repository.obtenerNatacionCompleta(
            usuarioId
        )
}