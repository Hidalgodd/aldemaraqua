package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.MuestraRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HistorialViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = MuestraRepository(
		AppDatabase.obtenerInstancia(application).muestraDao()
	)

	val muestras = repository.muestras.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = emptyList()
	)
}