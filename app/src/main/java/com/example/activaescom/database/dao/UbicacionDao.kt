package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.activaescom.database.entities.UbicacionEntity

@Dao
interface UbicacionDao {

    @Insert
    suspend fun insertarUbicacion(ubicacion: UbicacionEntity)

    @Query("""
        SELECT * FROM ubicaciones
        WHERE actividadId = :actividadId
    """)
    suspend fun obtenerUbicacionesActividad(
        actividadId: Int
    ): List<UbicacionEntity>
}