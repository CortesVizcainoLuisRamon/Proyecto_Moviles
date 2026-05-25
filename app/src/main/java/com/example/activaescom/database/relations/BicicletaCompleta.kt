package com.example.activaescom.database.relations

import androidx.room.Embedded
import androidx.room.Relation

import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.entities.BicicletaConfiguracionEntity
import com.example.activaescom.database.entities.BicicletaDetalleEntity

data class BicicletaCompleta(

    @Embedded
    val entrenamiento:
    EntrenamientoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val configuracion:
    BicicletaConfiguracionEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val detalle:
    BicicletaDetalleEntity?
)