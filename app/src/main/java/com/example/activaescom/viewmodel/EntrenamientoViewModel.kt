package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.EntrenamientoEntity
import com.example.activaescom.repository.EntrenamientoRepository
import kotlinx.coroutines.launch
import com.example.activaescom.database.relations.CarreraCompleta

class EntrenamientoViewModel(

    application: Application

) : AndroidViewModel(application) {

    private val repository: EntrenamientoRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .entrenamientoDao()

        repository =
            EntrenamientoRepository(dao)
    }

    suspend fun insertarEntrenamiento(
        entrenamiento: EntrenamientoEntity
    ): Long {

        return repository
            .insertarEntrenamiento(
                entrenamiento
            )
    }

    suspend fun obtenerEntrenamientosUsuario(
        usuarioId: Int
    ): List<EntrenamientoEntity> {

        return repository
            .obtenerEntrenamientosUsuario(
                usuarioId
            )
    }

    suspend fun obtenerCarrerasCompletas(
        usuarioId: Int
    ): List<CarreraCompleta> {

        return repository
            .obtenerCarrerasCompletas(
                usuarioId
            )
    }
}