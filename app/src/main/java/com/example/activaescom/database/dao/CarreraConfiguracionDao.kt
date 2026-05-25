package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.activaescom.database.entities.CarreraConfiguracionEntity

@Dao
interface CarreraConfiguracionDao {

    @Insert
    suspend fun insertarConfiguracionCarrera(
        configuracion: CarreraConfiguracionEntity
    )

    @Query("""
        SELECT * FROM carrera_configuracion
        WHERE entrenamientoId = :entrenamientoId
        LIMIT 1
    """)
    suspend fun obtenerConfiguracionCarrera(
        entrenamientoId: Int
    ): CarreraConfiguracionEntity?
}