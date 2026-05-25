package com.example.activaescom.database.repository

import com.example.activaescom.database.dao.FuerzaDao
import com.example.activaescom.database.entities.FuerzaConfiguracionEntity
import com.example.activaescom.database.entities.FuerzaDetalleEntity

class FuerzaRepository(

    private val fuerzaDao:
    FuerzaDao
) {

    suspend fun insertarConfiguracion(
        configuracion:
        FuerzaConfiguracionEntity
    ) {

        fuerzaDao
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        FuerzaDetalleEntity
    ) {

        fuerzaDao
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerFuerzaCompleta(
        usuarioId: Int
    ) =
        fuerzaDao.obtenerFuerzaCompleta(
            usuarioId
        )
}