package com.example.activaescom.repository

import com.example.activaescom.database.dao.CarreraConfiguracionDao
import com.example.activaescom.database.entities.CarreraConfiguracionEntity

class CarreraConfiguracionRepository(

    private val dao: CarreraConfiguracionDao

) {

    suspend fun insertarConfiguracionCarrera(
        configuracion: CarreraConfiguracionEntity
    ) {

        dao.insertarConfiguracionCarrera(
            configuracion
        )
    }

    suspend fun obtenerConfiguracionCarrera(
        entrenamientoId: Int
    ): CarreraConfiguracionEntity? {

        return dao.obtenerConfiguracionCarrera(
            entrenamientoId
        )
    }
}