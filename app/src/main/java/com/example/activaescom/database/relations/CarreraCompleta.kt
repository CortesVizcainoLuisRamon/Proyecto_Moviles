package com.example.activaescom.database.relations

import androidx.room.Embedded
import androidx.room.Relation

import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.entities.CarreraConfiguracionEntity
import com.example.activaescom.database.entities.CarreraDetalleEntity

data class CarreraCompleta(

    @Embedded
    val entrenamiento:
    EntrenamientoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val configuracion:
    CarreraConfiguracionEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val detalle:
    CarreraDetalleEntity?
)