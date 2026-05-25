package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.CarreraDetalleEntity
import com.example.activaescom.repository.CarreraDetalleRepository

import kotlinx.coroutines.launch

class CarreraDetalleViewModel(

    application: Application

) : AndroidViewModel(application) {

    private val repository:
            CarreraDetalleRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .carreraDetalleDao()

        repository =
            CarreraDetalleRepository(dao)
    }

    fun insertarDetalleCarrera(
        detalle: CarreraDetalleEntity
    ) {

        viewModelScope.launch {

            repository
                .insertarDetalleCarrera(
                    detalle
                )
        }
    }
}