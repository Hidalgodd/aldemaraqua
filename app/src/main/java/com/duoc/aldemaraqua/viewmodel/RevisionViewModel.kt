package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.MuestraRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class RevisionViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = MuestraRepository(
		AppDatabase.obtenerInstancia(application).muestraDao()
	)

	private val _muestra = MutableStateFlow<Muestra?>(null)
	val muestra = _muestra.asStateFlow()

	private val _cargando = MutableStateFlow(false)
	val cargando = _cargando.asStateFlow()

	fun cargar(id: Long) {
		viewModelScope.launch {
			_cargando.value = true
			try {
				_muestra.value = repository.obtenerPorId(id)
			} finally {
				_cargando.value = false
			}
		}
	}

	fun guardarRevision(
		estado: String,
		comentario: String,
		alGuardar: () -> Unit,
		siHayError: (String) -> Unit
	) {
		if (estado == "observado" && comentario.isBlank()) {
			siHayError("Agrega un comentario para indicar qué debe corregirse.")
			return
		}

		val muestraActual = _muestra.value
		if (muestraActual == null) {
			siHayError("No se encontró la muestra para revisar.")
			return
		}

		viewModelScope.launch {
			try {
				val muestraActualizada = muestraActual.copy(
					estadoRevision = estado,
					fechaRevision = LocalDateTime.now().toString(),
					comentarioSupervisor = comentario.trim()
				)
				repository.actualizar(muestraActualizada)
				_muestra.value = muestraActualizada
				alGuardar()
			} catch (error: Exception) {
				if (error is CancellationException) throw error
				siHayError("No se pudo guardar la revisión.")
			}
		}
	}
}