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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import com.duoc.aldemaraqua.util.Reglas
import com.duoc.aldemaraqua.util.Validador

class NuevaMuestraViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MuestraRepository(
        AppDatabase.obtenerInstancia(application).muestraDao()
    )

    private var guardadoEnCurso = false

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
        Validador.conteo(conteoEstimado, obligatorio = enviar)?.let(::add)
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
        // Evita crear dos registros si el operador toca dos veces "Enviar".
        if (guardadoEnCurso) return
        guardadoEnCurso = true
        viewModelScope.launch {
            var fotoCopiada: String? = null
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
                fotoCopiada = rutaFoto
                val estadoFinal = if (enviar) {
                    Reglas.estadoAlEnviar(muestraExistente?.estadoRevision ?: muestra.estadoRevision)
                        .name.lowercase()
                } else if (muestra.id == 0L) {
                    EstadoMuestra.BORRADOR.name.lowercase()
                } else {
                    muestraExistente?.estadoRevision ?: muestra.estadoRevision
                }
                val rutaFinal = rutaFoto ?: muestra.fotoPath
                val fotoExiste = withContext(Dispatchers.IO) {
                    !rutaFinal.isNullOrBlank() && File(rutaFinal).isFile
                }
                if (enviar && !fotoExiste) {
                    siHayError("La fotografía no está disponible. Vuelve a adjuntarla.")
                    return@launch
                }
                val muestraFinal = muestra.copy(
                    fotoPath = rutaFoto ?: muestra.fotoPath,
                    estadoRevision = estadoFinal,
                    operador = muestraExistente?.operador ?: usuario.codigo,
                    revisadaPorId = muestraExistente?.revisadaPorId,
                    revisadaPor = muestraExistente?.revisadaPor,
                    fechaRevision = muestraExistente?.fechaRevision,
                    comentarioSupervisor = muestraExistente?.comentarioSupervisor.orEmpty(),
                    // Cualquier modificación invalida un envío de demostración previo.
                    sincronizada = false
                )
                if (muestraFinal.id == 0L) {
                    repository.guardar(muestraFinal)
                } else {
                    repository.actualizar(muestraFinal)
                }
                // Ya fue guardada en Room: no se debe limpiar la nueva foto.
                fotoCopiada = null
                // Borra la foto antigua solo después de persistir el nuevo registro.
                if (muestraExistente != null && muestraExistente.fotoPath != muestraFinal.fotoPath) {
                    muestraExistente.fotoPath?.let { antigua ->
                        withContext(Dispatchers.IO) { File(antigua).delete() }
                    }
                }
                alTerminar()
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                siHayError("No se pudo guardar la muestra. Inténtalo nuevamente.")
            } finally {
                // Si falló la validación o persistencia, evita fotografías huérfanas.
                fotoCopiada?.let { ruta ->
                    withContext(NonCancellable + Dispatchers.IO) { File(ruta).delete() }
                }
                guardadoEnCurso = false
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
        val archivo = File.createTempFile("muestra_", ".$extension", carpetaFotos)
        try {
            val entrada = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("No se pudo leer la foto seleccionada")
            entrada.use { input ->
                archivo.outputStream().use { output -> input.copyTo(output) }
            }
            return archivo.absolutePath
        } catch (error: Exception) {
            archivo.delete() // No conserva copias incompletas tras un fallo de lectura.
            throw error
        }
    }
}