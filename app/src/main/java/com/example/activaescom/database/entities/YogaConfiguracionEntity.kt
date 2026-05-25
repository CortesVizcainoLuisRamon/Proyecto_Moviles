package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class YogaConfiguracionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val entrenamientoId: Int,

    val estilo: String,

    val nivel: String,

    val savasanaActivo: Boolean,

    val minutosSavasana: Int
)