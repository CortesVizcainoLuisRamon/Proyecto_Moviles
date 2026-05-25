package com.example.activaescom.database.repository

import com.example.activaescom.database.dao.YogaDao
import com.example.activaescom.database.entities.YogaConfiguracionEntity
import com.example.activaescom.database.entities.YogaDetalleEntity

class YogaRepository(

    private val yogaDao:
    YogaDao
) {

    suspend fun insertarConfiguracion(
        configuracion:
        YogaConfiguracionEntity
    ) {

        yogaDao
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        YogaDetalleEntity
    ) {

        yogaDao
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerYogaCompleto(
        usuarioId: Int
    ) =
        yogaDao.obtenerYogaCompleto(
            usuarioId
        )
}