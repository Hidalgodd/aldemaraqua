package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.MuestraRepository
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.util.Reglas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val cargando: Boolean = true,
    val muestras: List<Muestra> = emptyList(),
    val texto: String = "",
    val estado: EstadoMuestra? = null
)

class HistorialViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = MuestraRepository(
		AppDatabase.obtenerInstancia(application).muestraDao()
	)

	private val _todasLasMuestras = MutableStateFlow<List<Muestra>>(emptyList())
	val muestras = _todasLasMuestras.asStateFlow()

	private val _uiState = MutableStateFlow(HistorialUiState())
	val uiState = _uiState.asStateFlow()

	private var usuario: UsuarioDemo? = null

	init {
		viewModelScope.launch {
			repository.muestras.collect { lista ->
				_todasLasMuestras.value = lista
				aplicarFiltros()
			}
		}
	}

	fun establecerUsuario(usuario: UsuarioDemo) {
		if (this.usuario != usuario) {
			_uiState.update { it.copy(texto = "", estado = null) }
		}
		this.usuario = usuario
		aplicarFiltros()
	}

	fun onTextoChange(texto: String) {
		_uiState.update { it.copy(texto = texto) }
		aplicarFiltros()
	}

	fun onEstadoChange(estado: EstadoMuestra?) {
		_uiState.update { it.copy(estado = estado) }
		aplicarFiltros()
	}

	private fun aplicarFiltros() {
		val usuarioActual = usuario
		_uiState.update { state ->
			val visibles = usuarioActual?.let { actual ->
				_todasLasMuestras.value.filter { Reglas.esVisible(actual, it) }
			}.orEmpty()
			state.copy(
				cargando = false,
				muestras = Reglas.filtrar(visibles, state.texto, state.estado)
			)
		}
	}
}