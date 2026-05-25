package com.example.activaescom.repository

import com.example.activaescom.database.dao.EntrenamientoDao
import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.relations.CarreraCompleta

class EntrenamientoRepository(

    private val entrenamientoDao: EntrenamientoDao

) {

    suspend fun insertarEntrenamiento(
        entrenamiento: EntrenamientoEntity
    ): Long {

        return entrenamientoDao
            .insertarEntrenamiento(
                entrenamiento
            )
    }

    suspend fun obtenerEntrenamientosUsuario(
        usuarioId: Int
    ): List<EntrenamientoEntity> {

        return entrenamientoDao
            .obtenerEntrenamientosUsuario(
                usuarioId
            )
    }

    suspend fun obtenerCarrerasCompletas(
        usuarioId: Int
    ): List<CarreraCompleta> {

        return entrenamientoDao
            .obtenerCarrerasCompletas(
                usuarioId
            )
    }
}