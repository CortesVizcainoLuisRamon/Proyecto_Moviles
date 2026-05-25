package com.example.activaescom.database.relations

import androidx.room.Embedded
import androidx.room.Relation

import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.database.entities.CaminataConfiguracionEntity
import com.example.activaescom.database.entities.CaminataDetalleEntity

data class CaminataCompleta(

    @Embedded
    val entrenamiento:
    EntrenamientoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val configuracion:
    CaminataConfiguracionEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "entrenamientoId"
    )
    val detalle:
    CaminataDetalleEntity?
)