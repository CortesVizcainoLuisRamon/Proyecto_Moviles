package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entrenamientos")

data class EntrenamientoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioId: Int,

    // DATOS GENERALES

    val tipoActividad: String,

    val nombreEntrenamiento: String,

    val fechaEntrenamiento: String,

    val lugarEntrenamiento: String,

    val horasSueno: Int,

    val duracionEstimada: Int,

    val metaKmObjetivo: Double,

    val notas: String,

    // RESULTADOS GENERALES

    val duracionRealSegundos: Long,

    val caloriasQuemadas: Int,

    // FECHA REAL DEL REGISTRO

    val timestamp: Long
)