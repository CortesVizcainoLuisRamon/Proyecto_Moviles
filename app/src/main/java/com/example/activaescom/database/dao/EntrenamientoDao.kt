package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.activaescom.database.relations.CarreraCompleta
import com.example.activaescom.database.entities.EntrenamientoEntity

@Dao
interface EntrenamientoDao {

    @Insert
    suspend fun insertarEntrenamiento(
        entrenamiento: EntrenamientoEntity
    ): Long

    @Query("""
        SELECT * FROM entrenamientos
        WHERE usuarioId = :usuarioId
        ORDER BY timestamp DESC
    """)
    suspend fun obtenerEntrenamientosUsuario(
        usuarioId: Int
    ): List<EntrenamientoEntity>

    @Transaction
    @Query("""
        SELECT * FROM entrenamientos
        WHERE tipoActividad = 'Carrera'
        AND usuarioId = :usuarioId
        ORDER BY timestamp DESC
    """)
    suspend fun obtenerCarrerasCompletas(
        usuarioId: Int
    ): List<CarreraCompleta>

    @Query("""
        UPDATE entrenamientos
        SET duracionRealSegundos = :duracion,
            caloriasQuemadas = :calorias,
            rutaMapa = :rutaMapa
        WHERE id = :entrenamientoId
    """)
    suspend fun actualizarResultados(
        entrenamientoId: Int,
        duracion: Long,
        calorias: Int,
        rutaMapa: String?
    )
}