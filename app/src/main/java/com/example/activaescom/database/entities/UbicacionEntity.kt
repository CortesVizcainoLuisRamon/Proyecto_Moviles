package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ubicaciones")
data class UbicacionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val actividadId: Int,

    val latitud: Double,

    val longitud: Double,

    val timestamp: Long,

    val velocidad: Float
)