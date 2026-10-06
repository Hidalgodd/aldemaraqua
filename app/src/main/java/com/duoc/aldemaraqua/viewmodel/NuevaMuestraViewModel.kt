package com.duoc.aldemaraqua.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.aldemaraqua.data.AppDatabase
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.MuestraRepository
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class NuevaMuestraViewModel(application: Application) : AndroidViewModel(application) {
	private val repository = MuestraRepository(
		AppDatabase.obtenerInstancia(application).muestraDao()
	)

	fun guardar(
		muestra: Muestra,
		fotoUri: Uri?,
		alTerminar: () -> Unit,
		siHayError: (String) -> Unit
	) {
		viewModelScope.launch {
			try {
				val rutaFoto = fotoUri?.let { uri ->
					withContext(Dispatchers.IO) { copiarFoto(uri) }
				}
				repository.guardar(muestra.copy(fotoPath = rutaFoto))
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