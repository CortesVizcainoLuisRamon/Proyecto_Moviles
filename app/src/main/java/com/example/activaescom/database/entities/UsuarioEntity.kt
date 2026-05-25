package com.example.activaescom.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "usuarios",
    indices = [
        Index(
            value = ["correo"],
            unique = true
        )
    ]
)
data class UsuarioEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuario: String,

    val nombre: String,

    val apellido: String,

    val correo: String,

    val fechaNacimiento: String,

    val fotoPerfil: String,

    val password: String,

    val boleta: String
)