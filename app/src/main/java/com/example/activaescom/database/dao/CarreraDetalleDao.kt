package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.activaescom.database.entities.CarreraDetalleEntity

@Dao
interface CarreraDetalleDao {

    @Insert
    suspend fun insertarDetalleCarrera(
        detalle: CarreraDetalleEntity
    )

    @Query("""
        SELECT * FROM carrera_detalles
        WHERE entrenamientoId = :entrenamientoId
        LIMIT 1
    """)
    suspend fun obtenerDetalleCarrera(
        entrenamientoId: Int
    ): CarreraDetalleEntity?
}