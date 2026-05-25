package com.example.activaescom.database.relations

import androidx.room.Embedded
import androidx.room.Relation

import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.entities.FuerzaConfiguracionEntity
import com.example.activaescom.database.entities.FuerzaDetalleEntity

data class FuerzaCompleta(

    @Embedded
    val entrenamiento:
    EntrenamientoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val configuracion:
    FuerzaConfiguracionEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val detalle:
    FuerzaDetalleEntity?
)