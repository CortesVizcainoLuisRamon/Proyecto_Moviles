package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BicicletaDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val distanciaKm: Double,

    val velocidadPromedio: Double,

    val caloriasQuemadas: Int,

    val duracionSegundos: Long
)