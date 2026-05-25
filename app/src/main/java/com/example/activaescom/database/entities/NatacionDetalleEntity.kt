package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NatacionDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val distanciaKm: Double,

    val vueltasTotales: Int,

    val caloriasQuemadas: Int,

    val duracionSegundos: Long
)