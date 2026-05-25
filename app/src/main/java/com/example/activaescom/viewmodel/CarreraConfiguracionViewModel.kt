package com.example.activaescom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.activaescom.database.AppDatabase
import com.example.activaescom.database.entities.CarreraConfiguracionEntity
import com.example.activaescom.repository.CarreraConfiguracionRepository

import kotlinx.coroutines.launch

class CarreraConfiguracionViewModel(

    application: Application

) : AndroidViewModel(application) {

    private val repository:
            CarreraConfiguracionRepository

    init {

        val dao =
            AppDatabase
                .getDatabase(application)
                .carreraConfiguracionDao()

        repository =
            CarreraConfiguracionRepository(dao)
    }

    fun insertarConfiguracionCarrera(
        configuracion: CarreraConfiguracionEntity
    ) {

        viewModelScope.launch {

            repository
                .insertarConfiguracionCarrera(
                    configuracion
                )
        }
    }
}