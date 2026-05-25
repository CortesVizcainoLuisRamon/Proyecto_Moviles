package com.example.activaescom.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.activaescom.database.entities.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios\n" +
            "ORDER BY id DESC\n" +
            "LIMIT 1")
    suspend fun obtenerUsuario(): UsuarioEntity?

    @Query("""
    SELECT * FROM usuarios
    WHERE correo = :correo
    AND password = :password
    LIMIT 1
""")
    suspend fun login(
        correo: String,
        password: String
    ): UsuarioEntity?

    @Query("""
    SELECT * FROM usuarios
    WHERE correo = :correo
    LIMIT 1
""")
    suspend fun obtenerUsuarioPorCorreo(
        correo: String
    ): UsuarioEntity?

    @Query("""
    SELECT * FROM usuarios
    WHERE id = :id
    LIMIT 1
""")
    suspend fun obtenerUsuarioPorId(
        id: Int
    ): UsuarioEntity?

}