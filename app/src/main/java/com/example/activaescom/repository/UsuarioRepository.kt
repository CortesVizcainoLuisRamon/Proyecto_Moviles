package com.example.activaescom.repository

import com.example.activaescom.database.dao.UsuarioDao
import com.example.activaescom.database.entities.UsuarioEntity

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun insertarUsuario(
        usuario: UsuarioEntity
    ) {
        usuarioDao.insertarUsuario(usuario)
    }

    suspend fun obtenerUsuario(): UsuarioEntity? {

        return usuarioDao.obtenerUsuario()
    }

    suspend fun actualizarUsuario(
        usuario: UsuarioEntity
    ) {
        usuarioDao.actualizarUsuario(usuario)
    }

    suspend fun login(
        correo: String,
        password: String
    ): UsuarioEntity? {

        return usuarioDao.login(correo, password)
    }

    suspend fun obtenerUsuarioPorCorreo(
        correo: String
    ): UsuarioEntity? {

        return usuarioDao.obtenerUsuarioPorCorreo(correo)
    }

    suspend fun obtenerUsuarioPorId(
        id: Int
    ): UsuarioEntity? {

        return usuarioDao.obtenerUsuarioPorId(id)
    }
}