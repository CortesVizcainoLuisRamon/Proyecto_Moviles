package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CaminataConfiguracionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val intensidadPaso: String,

    val terreno: String,

    val calentamientoActivo: Boolean,

    val minutosCalentamiento: Int,

    val acompanante: String
)