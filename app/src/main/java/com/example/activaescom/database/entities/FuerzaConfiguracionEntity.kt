package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FuerzaConfiguracionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val calentamientoActivo: Boolean,

    val minutosCalentamiento: Int,

    val grupoMuscular: String
)