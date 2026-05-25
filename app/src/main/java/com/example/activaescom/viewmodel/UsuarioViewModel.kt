package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.UsuarioEntity
import com.example.activaescom.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: UsuarioRepository

    init {

        val usuarioDao =
            AppDatabase
                .getDatabase(application.applicationContext)
                .usuarioDao()

        repository = UsuarioRepository(usuarioDao)
    }

    fun guardarUsuario(usuario: UsuarioEntity) {

        viewModelScope.launch {

            repository.insertarUsuario(usuario)
        }
    }

    suspend fun login(
        correo: String,
        password: String
    ): UsuarioEntity? {

        return repository.login(correo, password)
    }

    suspend fun obtenerUsuario(): UsuarioEntity? {

        return repository.obtenerUsuario()
    }

    fun actualizarUsuario(
        usuario: UsuarioEntity
    ) {

        viewModelScope.launch {

            repository.actualizarUsuario(usuario)
        }
    }

    suspend fun obtenerUsuarioPorCorreo(
        correo: String
    ): UsuarioEntity? {

        return repository.obtenerUsuarioPorCorreo(correo)
    }

    suspend fun obtenerUsuarioPorId(
        id: Int
    ): UsuarioEntity? {

        return repository.obtenerUsuarioPorId(id)
    }
}