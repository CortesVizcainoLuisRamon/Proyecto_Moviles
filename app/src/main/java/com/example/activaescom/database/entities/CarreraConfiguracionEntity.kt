package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrera_configuracion")

data class CarreraConfiguracionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // RELACIÓN

    val entrenamientoId: Int,

    // CONFIGURACIÓN

    val objetivo: String,

    val zonaCardiaca: String,

    val superficie: String,

    val calentamientoActivo: Boolean,

    val minutosCalentamiento: Int,

    val avisosVoz: String
)