package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class NatacionConfiguracionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val tamanoAlberca: Int,

    val nivel: String,

    val estilo: String,

    val calentamientoActivo: Boolean,

    val minutosCalentamiento: Int
)