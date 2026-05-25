package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CaminataDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val distanciaKm: Double,

    val ritmo: String,

    val caloriasQuemadas: Int,

    val duracionSegundos: Long
)