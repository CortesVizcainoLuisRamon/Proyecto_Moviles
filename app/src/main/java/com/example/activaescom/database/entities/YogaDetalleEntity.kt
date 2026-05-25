package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class YogaDetalleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val caloriasQuemadas: Int,

    val ciclosRespiracion: Int,

    val duracionSegundos: Long
)