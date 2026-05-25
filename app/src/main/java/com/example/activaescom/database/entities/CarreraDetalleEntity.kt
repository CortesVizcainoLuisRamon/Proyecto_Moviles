package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrera_detalles")

data class CarreraDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // RELACIÓN
    val entrenamientoId: Int,

    // MÉTRICAS

    val distanciaKm: Double,

    val caloriasQuemadas: Int,

    val duracionSegundos: Long,

    val ritmoPromedio: String,

    val velocidadPromedio: Double,

    val pasos: Int
)