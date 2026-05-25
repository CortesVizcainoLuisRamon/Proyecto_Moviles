package com.example.activaescom.database.repository

import com.example.activaescom.database.dao.BicicletaDao
import com.example.activaescom.database.entities.BicicletaConfiguracionEntity
import com.example.activaescom.database.entities.BicicletaDetalleEntity

class BicicletaRepository(

    private val bicicletaDao:
    BicicletaDao
) {

    suspend fun insertarConfiguracion(
        configuracion:
        BicicletaConfiguracionEntity
    ) {

        bicicletaDao
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        BicicletaDetalleEntity
    ) {

        bicicletaDao
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerBicicletaCompleta(
        usuarioId: Int
    ) =
        bicicletaDao.obtenerBicicletaCompleta(
            usuarioId
        )
}