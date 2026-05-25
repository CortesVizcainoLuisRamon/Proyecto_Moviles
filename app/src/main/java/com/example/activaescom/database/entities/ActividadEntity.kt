package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class ActividadEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val tipoActividad: String,

    val fechaInicio: Long,

    val fechaFin: Long,

    val duracion: Long,

    val distancia: Double,

    val calorias: Double,

    val velocidadPromedio: Double
)