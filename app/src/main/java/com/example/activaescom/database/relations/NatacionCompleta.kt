package com.example.activaescom.database.relations

import androidx.room.Embedded
import androidx.room.Relation

import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.entities.NatacionConfiguracionEntity
import com.example.activaescom.database.entities.NatacionDetalleEntity

data class NatacionCompleta(

    @Embedded
    val entrenamiento:
    EntrenamientoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val configuracion:
    NatacionConfiguracionEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val detalle:
    NatacionDetalleEntity?
)