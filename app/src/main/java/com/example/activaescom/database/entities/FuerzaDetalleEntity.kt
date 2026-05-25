package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FuerzaDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val seriesObjetivo: Int,

    val repeticionesObjetivo: Int,

    val pesoObjetivo: Double,

    val volumenTotal: Double,

    val duracionSegundos: Long
)