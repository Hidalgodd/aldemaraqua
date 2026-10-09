package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.MuestraRepository
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.util.ExportadorCsv
import com.duoc.aldemaraqua.util.Reglas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

data class SincronizacionUiState(
    val sincronizando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false
)

data class ExportacionUiState(
    val exportando: Boolean = false,
    val mensaje: String? = null,
    val esError: Boolean = false
)

class ResumenViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MuestraRepository(
        AppDatabase.obtenerInstancia(application).muestraDao()
    )

    private val _sincronizacion = MutableStateFlow(SincronizacionUiState())
    val sincronizacion = _sincronizacion.asStateFlow()

    private val _exportacion = MutableStateFlow(ExportacionUiState())
    val exportacion = _exportacion.asStateFlow()

    fun exportarCsv(uri: Uri, usuario: UsuarioDemo, muestras: List<Muestra>) {
        if (usuario.rol != Rol.ANALISTA) {
            _exportacion.value = ExportacionUiState(
                mensaje = "Solo el perfil analista puede exportar muestras.",
                esError = true
            )
            return
        }
        if (_exportacion.value.exportando) return

        viewModelScope.launch {
            _exportacion.value = ExportacionUiState(exportando = true)
            try {
                val validadas = muestras.filter {
                    Reglas.esVisible(usuario, it) &&
                        EstadoMuestra.desde(it.estadoRevision) == EstadoMuestra.VALIDADO
                }
                val contenido = ExportadorCsv.generar(validadas)
                withContext(Dispatchers.IO) {
                    val salida = getApplication<Application>().contentResolver
                        .openOutputStream(uri)
                        ?: throw IOException("No se pudo crear el archivo CSV seleccionado.")
                    salida.bufferedWriter(Charsets.UTF_8).use { writer ->
                        writer.write(contenido)
                    }
                }
                _exportacion.value = ExportacionUiState(
                    mensaje = "Se exportaron ${validadas.size} muestras validadas."
                )
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _exportacion.value = ExportacionUiState(
                    mensaje = error.message ?: "No se pudo exportar el archivo CSV.",
                    esError = true
                )
            }
        }
    }

    fun sincronizar() {
        if (_sincronizacion.value.sincronizando) return
        viewModelScope.launch {
            _sincronizacion.value = SincronizacionUiState(sincronizando = true)
            try {
                val total = repository.sincronizarValidadas()
                _sincronizacion.value = SincronizacionUiState(
                    mensaje = if (total == 0) {
                        "No hay muestras validadas pendientes de sincronizar."
                    } else {
                        "Se sincronizaron $total muestras validadas."
                    }
                )
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _sincronizacion.value = SincronizacionUiState(
                    mensaje = error.message ?: "No se pudo sincronizar. Revisa la conexión e inténtalo nuevamente.",
                    esError = true
                )
            }
        }
    }
}
