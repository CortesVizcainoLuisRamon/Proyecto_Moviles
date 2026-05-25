package com.example.activaescom.repository

import com.example.activaescom.database.dao.CarreraDetalleDao
import com.example.activaescom.database.entities.CarreraDetalleEntity

class CarreraDetalleRepository(

    private val dao: CarreraDetalleDao

) {

    suspend fun insertarDetalleCarrera(
        detalle: CarreraDetalleEntity
    ) {

        dao.insertarDetalleCarrera(
            detalle
        )
    }

    suspend fun obtenerDetalleCarrera(
        entrenamientoId: Int
    ): CarreraDetalleEntity? {

        return dao.obtenerDetalleCarrera(
            entrenamientoId
        )
    }
}