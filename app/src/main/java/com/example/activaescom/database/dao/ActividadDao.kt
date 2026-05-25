package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.activaescom.database.entities.ActividadEntity

@Dao
interface ActividadDao {

    @Insert
    suspend fun insertarActividad(actividad: ActividadEntity)

    @Query("""
        SELECT * FROM actividades
        ORDER BY fechaInicio DESC
    """)
    suspend fun obtenerActividades(): List<ActividadEntity>
}