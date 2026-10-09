package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.MuestraRepository
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.util.Reglas
import com.duoc.aldemaraqua.util.Validador
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
		usuario: UsuarioDemo,
		estado: String,
		comentario: String,
		alGuardar: () -> Unit,
		siHayError: (String) -> Unit
	) {
		if (estado !in setOf(
				EstadoMuestra.OBSERVADO.name.lowercase(),
				EstadoMuestra.VALIDADO.name.lowercase()
			)
		) {
			siHayError("Selecciona un resultado de revisión válido.")
			return
		}

		val muestraActual = _muestra.value
		if (muestraActual == null) {
			siHayError("No se encontró la muestra para revisar.")
			return
		}
		if (!Reglas.puedeRevisar(usuario, muestraActual)) {
			siHayError("Esta muestra ya no está disponible para revisión.")
			return
		}
		if (estado == EstadoMuestra.OBSERVADO.name.lowercase()) {
			Validador.comentarioRevision(comentario)?.let {
				siHayError(it)
				return
			}
		}

		viewModelScope.launch {
			try {
				val muestraActualizada = muestraActual.copy(
					estadoRevision = estado,
					revisadaPor = usuario.codigo,
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