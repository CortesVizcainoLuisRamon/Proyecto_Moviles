package com.example.activaescom.database.repository

import com.example.activaescom.database.dao.NatacionDao
import com.example.activaescom.database.entities.NatacionConfiguracionEntity
import com.example.activaescom.database.entities.NatacionDetalleEntity

class NatacionRepository(

    private val natacionDao:
    NatacionDao
) {

    suspend fun insertarConfiguracion(
        configuracion:
        NatacionConfiguracionEntity
    ) {

        natacionDao
            .insertarConfiguracion(
                configuracion
            )
    }

    suspend fun insertarDetalle(
        detalle:
        NatacionDetalleEntity
    ) {

        natacionDao
            .insertarDetalle(
                detalle
            )
    }

    suspend fun obtenerNatacionCompleta(
        usuarioId: Int
    ) =
        natacionDao.obtenerNatacionCompleta(
            usuarioId
        )
}