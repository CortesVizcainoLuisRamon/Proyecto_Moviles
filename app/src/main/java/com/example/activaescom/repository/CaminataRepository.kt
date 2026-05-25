package com.example.activaescom.database.repository

import com.example.activaescom.database.dao.CaminataDao
import com.example.activaescom.database.entities.CaminataConfiguracionEntity
import com.example.activaescom.database.entities.CaminataDetalleEntity

class CaminataRepository(

    private val caminataDao:
    CaminataDao
) {

    suspend fun insertarConfiguracion(
        configuracion:
        CaminataConfiguracionEntity
    ) {

        caminataDao
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        CaminataDetalleEntity
    ) {

        caminataDao
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerCaminataCompleta(
        usuarioId: Int
    ) =
        caminataDao.obtenerCaminataCompleta(
            usuarioId
        )
    
}