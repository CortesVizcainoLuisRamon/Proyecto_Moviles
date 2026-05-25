package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BicicletaConfiguracionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val tipoBicicleta: String,

    val terreno: String,

    val objetivo: String,

    val calentamientoActivo: Boolean,

    val minutosCalentamiento: Int,

    val esEBike: Boolean,

    val avisosRuta: String
)