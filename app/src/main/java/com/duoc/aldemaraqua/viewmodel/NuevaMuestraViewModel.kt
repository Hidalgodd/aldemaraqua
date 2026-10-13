package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.MuestraRepository
import android.webkit.MimeTypeMap
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.UsuarioDemo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.duoc.aldemaraqua.util.Reglas
import com.duoc.aldemaraqua.util.Validador

class NuevaMuestraViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = MuestraRepository(
		AppDatabase.obtenerInstancia(application).muestraDao()
	)

	suspend fun obtener(id: Long): Muestra? = repository.obtenerPorId(id)

	fun validar(
		muestra: Muestra,
		tramoCm: String,
		conteoEstimado: String,
		conteoConfirmado: String,
		calibre: String,
		enviar: Boolean,
		fotoAdjunta: Boolean
	): List<String> = buildList {
		Validador.requerido(muestra.centro, "Selecciona el centro")?.let(::add)
		Validador.requerido(muestra.tren, "Selecciona el tren")?.let(::add)
		Validador.requerido(muestra.linea, "Selecciona la línea")?.let(::add)

		Validador.tramoCm(tramoCm)?.let(::add)
		Validador.conteo(conteoEstimado, obligatorio = false)?.let(::add)
		Validador.conteo(conteoConfirmado, obligatorio = false)?.let(::add)
		Validador.conteo(
			conteoConfirmado.ifBlank { conteoEstimado },
			obligatorio = enviar
		)?.let(::add)
		Validador.calibre(calibre)?.let(::add)
		Validador.observaciones(muestra.observaciones)?.let(::add)
		Validador.foto(if (fotoAdjunta) "adjunta" else null, enviar)?.let(::add)
	}

	fun guardar(
		muestra: Muestra,
		usuario: UsuarioDemo,
		fotoUri: Uri?,
		enviar: Boolean,
		alTerminar: () -> Unit,
		siHayError: (String) -> Unit
	) {
		viewModelScope.launch {
			try {
				if (muestra.id == 0L && !Reglas.puedeCrear(usuario)) {
					siHayError("Solo un operador puede registrar muestras.")
					return@launch
				}
				val muestraExistente = if (muestra.id == 0L) {
					null
				} else {
					repository.obtenerPorId(muestra.id)
						?: throw IllegalArgumentException("No se encontró la muestra que intentas guardar.")
				}
				if (muestraExistente != null && !Reglas.puedeEditar(usuario, muestraExistente)) {
					siHayError("No tienes permiso para editar esta muestra.")
					return@launch
				}
				val rutaFoto = fotoUri?.let { uri ->
					withContext(Dispatchers.IO) { copiarFoto(uri) }
				}
				val estadoFinal = if (enviar) {
					Reglas.estadoAlEnviar(muestraExistente?.estadoRevision ?: muestra.estadoRevision)
						.name.lowercase()
				} else if (muestra.id == 0L) {
					EstadoMuestra.BORRADOR.name.lowercase()
				} else {
					muestraExistente?.estadoRevision ?: muestra.estadoRevision
				}
				val muestraFinal = muestra.copy(
					fotoPath = rutaFoto ?: muestra.fotoPath,
					estadoRevision = estadoFinal,
					operador = muestraExistente?.operador ?: usuario.codigo,
					revisadaPorId = muestraExistente?.revisadaPorId,
					revisadaPor = muestraExistente?.revisadaPor,
					fechaRevision = muestraExistente?.fechaRevision,
					comentarioSupervisor = muestraExistente?.comentarioSupervisor.orEmpty(),
					sincronizada = muestraExistente?.sincronizada ?: false
				)
				if (muestraFinal.id == 0L) {
					repository.guardar(muestraFinal)
				} else {
					repository.actualizar(muestraFinal)
				}
				alTerminar()
			} catch (error: Exception) {
				if (error is CancellationException) throw error
				siHayError("No se pudo guardar la muestra. Inténtalo nuevamente.")
			}
		}
	}

	private fun copiarFoto(uri: Uri): String {
		val context = getApplication<Application>()
		val carpetaFotos = File(context.filesDir, "fotos_muestras")
		if (!carpetaFotos.exists() && !carpetaFotos.mkdirs()) {
			throw IllegalStateException("No se pudo crear la carpeta de fotos")
		}

		val tipo = context.contentResolver.getType(uri)
		val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(tipo) ?: "jpg"
		val archivo = File(carpetaFotos, "muestra_${System.currentTimeMillis()}.$extension")
		val entrada = context.contentResolver.openInputStream(uri)
			?: throw IllegalStateException("No se pudo leer la foto seleccionada")

		entrada.use { input ->
			archivo.outputStream().use { output -> input.copyTo(output) }
		}
		return archivo.absolutePath
	}
}