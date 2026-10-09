package com.duoc.aldemaraqua.ui.screen

import android.net.Uri
import kotlinx.coroutines.CancellationException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.duoc.aldemaraqua.data.DatosDemo
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.viewmodel.NuevaMuestraViewModel
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMuestraScreen(
    viewModel: NuevaMuestraViewModel,
    usuario: UsuarioDemo,
    muestraId: Long,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var centro by remember(muestraId) { mutableStateOf("") }
    var tren by remember(muestraId) { mutableStateOf("") }
    var linea by remember(muestraId) { mutableStateOf("") }
    var concesion by remember(muestraId) { mutableStateOf("") }
    var fecha by remember(muestraId) { mutableStateOf(LocalDate.now().toString()) }
    var hora by remember(muestraId) {
        mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    }
    var tramoCm by remember(muestraId) { mutableStateOf("20") }
    var conteo by remember(muestraId) { mutableStateOf("") }
    var conteoConfirmado by remember(muestraId) { mutableStateOf("") }
    var calibre by remember(muestraId) { mutableStateOf("") }
    var observaciones by remember(muestraId) { mutableStateOf("") }
    var estadoActual by remember(muestraId) {
        mutableStateOf(EstadoMuestra.BORRADOR.name.lowercase())
    }
    var comentarioSupervisor by remember(muestraId) { mutableStateOf("") }
    var fotoPath by remember(muestraId) { mutableStateOf<String?>(null) }
    var fotoSeleccionada by remember(muestraId) { mutableStateOf<Uri?>(null) }
    var fotoCamaraTemporal by remember(muestraId) { mutableStateOf<File?>(null) }
    var muestraOriginal by remember(muestraId) { mutableStateOf<Muestra?>(null) }
    var errores by remember(muestraId) { mutableStateOf(emptyList<String>()) }
    var mensajeError by remember(muestraId) { mutableStateOf<String?>(null) }
    var cargando by remember(muestraId) { mutableStateOf(muestraId != 0L) }

    val selectorGaleria = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        fotoSeleccionada = uri
        errores = emptyList()
    }
    val capturaCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            fotoSeleccionada = fotoCamaraTemporal?.let { archivo ->
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    archivo
                )
            }
            errores = emptyList()
        } else {
            fotoCamaraTemporal?.delete()
            fotoCamaraTemporal = null
        }
    }

    LaunchedEffect(muestraId) {
        if (muestraId > 0) {
            try {
                val muestra = viewModel.obtener(muestraId)
                if (muestra != null) {
                    muestraOriginal = muestra
                    centro = muestra.centro
                    tren = muestra.tren
                    linea = muestra.linea
                    concesion = muestra.concesion.orEmpty()
                    fecha = muestra.fecha
                    hora = muestra.hora
                    tramoCm = (muestra.tramoMetros * 100).toInt().toString()
                    conteo = muestra.cantidadEstimadaML?.toString().orEmpty()
                    conteoConfirmado = muestra.cantidadConfirmada
                        ?.takeIf { it != muestra.cantidadEstimadaML }
                        ?.toString()
                        .orEmpty()
                    calibre = muestra.calibrePromedio?.toString().orEmpty()
                    observaciones = muestra.observaciones
                    estadoActual = muestra.estadoRevision
                    comentarioSupervisor = muestra.comentarioSupervisor
                    fotoPath = muestra.fotoPath
                } else {
                    mensajeError = "No se encontró la muestra que intentas editar."
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                mensajeError = "No se pudo cargar la muestra para editar."
            } finally {
                cargando = false
            }
        }
    }

    val fotoPreview: Any? = fotoSeleccionada ?: fotoPath?.let(::File)
    val fechaHora = "$fecha a las $hora"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (muestraId == 0L) "Nueva muestra" else "Editar muestra") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
            )
        }
    ) { innerPadding ->
        if (cargando) {
            Text("Cargando muestra...", modifier = Modifier.padding(innerPadding).padding(24.dp))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (estadoActual == EstadoMuestra.OBSERVADO.name.lowercase()) {
                    Text(
                        "Observación del supervisor: $comentarioSupervisor",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Text("Fecha: $fechaHora · Operador: ${usuario.codigo}")
                CatalogoDropdown(
                    valor = centro,
                    etiqueta = "Centro",
                    opciones = DatosDemo.centros,
                    onSeleccion = { centro = it }
                )
                OutlinedTextField(
                    value = concesion,
                    onValueChange = { concesion = it; errores = emptyList() },
                    label = { Text("Concesión (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.weight(1f)) {
                        CatalogoDropdown(
                            valor = tren,
                            etiqueta = "Tren",
                            opciones = DatosDemo.trenes,
                            onSeleccion = { tren = it }
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        CatalogoDropdown(
                            valor = linea,
                            etiqueta = "Línea",
                            opciones = DatosDemo.lineas,
                            onSeleccion = { linea = it }
                        )
                    }
                }
                OutlinedTextField(
                    value = tramoCm,
                    onValueChange = { tramoCm = it; errores = emptyList() },
                    label = { Text("Tramo muestreado (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val actual = conteo.toIntOrNull() ?: 0
                            conteo = (actual - 1).coerceAtLeast(0).toString()
                            errores = emptyList()
                        }
                    ) { Text("−1") }
                    OutlinedTextField(
                        value = conteo,
                        onValueChange = { conteo = it; errores = emptyList() },
                        label = { Text("Conteo estimado de individuos") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            conteo = ((conteo.toIntOrNull() ?: 0) + 1).toString()
                            errores = emptyList()
                        }
                    ) { Text("+1") }
                    Button(
                        onClick = {
                            conteo = ((conteo.toIntOrNull() ?: 0) + 10).toString()
                            errores = emptyList()
                        }
                    ) { Text("+10") }
                }
                OutlinedTextField(
                    value = conteoConfirmado,
                    onValueChange = { conteoConfirmado = it; errores = emptyList() },
                    label = { Text("Conteo confirmado o corregido (opcional)") },
                    supportingText = { Text("Déjalo vacío para aceptar la estimación.") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = calibre,
                    onValueChange = { calibre = it; errores = emptyList() },
                    label = { Text("Calibre promedio (mm, opcional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = observaciones,
                    onValueChange = { observaciones = it; errores = emptyList() },
                    label = { Text("Observaciones") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            val directorio = File(context.filesDir, "fotos_muestras")
                            if (!directorio.exists() && !directorio.mkdirs()) {
                                mensajeError = "No se pudo preparar el almacenamiento de la fotografía."
                            } else {
                                val archivo = File(directorio, "captura_${System.currentTimeMillis()}.jpg")
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        archivo
                                    )
                                    fotoCamaraTemporal = archivo
                                    capturaCamara.launch(uri)
                                } catch (error: IllegalArgumentException) {
                                    mensajeError = "No se pudo abrir la cámara en este dispositivo."
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Tomar foto") }
                    OutlinedButton(
                        onClick = { selectorGaleria.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) { Text("Elegir de galería") }
                }
                fotoPreview?.let { foto ->
                    AsyncImage(
                        model = foto,
                        contentDescription = "Fotografía de la muestra",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                    TextButton(onClick = {
                        fotoSeleccionada = null
                        fotoPath = null
                        fotoCamaraTemporal?.delete()
                        fotoCamaraTemporal = null
                    }) { Text("Quitar fotografía") }
                }
                errores.forEach { error ->
                    Text(error, color = MaterialTheme.colorScheme.error)
                }
                mensajeError?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            guardarMuestra(
                                viewModel = viewModel,
                                muestraId = muestraId,
                                usuario = usuario,
                                muestraOriginal = muestraOriginal,
                                centro = centro,
                                tren = tren,
                                linea = linea,
                                concesion = concesion,
                                fecha = fecha,
                                hora = hora,
                                tramoCm = tramoCm,
                                conteo = conteo,
                                conteoConfirmado = conteoConfirmado,
                                calibre = calibre,
                                observaciones = observaciones,
                                estadoActual = estadoActual,
                                fotoPath = fotoPath,
                                fotoUri = fotoSeleccionada,
                                enviar = false,
                                onErrores = { errores = it },
                                onError = { mensajeError = it },
                                onGuardado = {
                                    fotoCamaraTemporal?.delete()
                                    onGuardado()
                                }
                            )
                        },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) { Text("Guardar borrador") }
                    Button(
                        onClick = {
                            guardarMuestra(
                                viewModel = viewModel,
                                muestraId = muestraId,
                                usuario = usuario,
                                muestraOriginal = muestraOriginal,
                                centro = centro,
                                tren = tren,
                                linea = linea,
                                concesion = concesion,
                                fecha = fecha,
                                hora = hora,
                                tramoCm = tramoCm,
                                conteo = conteo,
                                conteoConfirmado = conteoConfirmado,
                                calibre = calibre,
                                observaciones = observaciones,
                                estadoActual = estadoActual,
                                fotoPath = fotoPath,
                                fotoUri = fotoSeleccionada,
                                enviar = true,
                                onErrores = { errores = it },
                                onError = { mensajeError = it },
                                onGuardado = {
                                    fotoCamaraTemporal?.delete()
                                    onGuardado()
                                }
                            )
                        },
                        modifier = Modifier.weight(1f).height(52.dp)
                    ) { Text("Enviar a revisión") }
                }
            }
        }
    }
}

@Composable
private fun CatalogoDropdown(
    valor: String,
    etiqueta: String,
    opciones: List<String>,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember(etiqueta) { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            label = { Text(etiqueta) },
            modifier = Modifier.fillMaxWidth().clickable { expandido = true }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}

private fun guardarMuestra(
    viewModel: NuevaMuestraViewModel,
    muestraId: Long,
    usuario: UsuarioDemo,
    muestraOriginal: Muestra?,
    centro: String,
    tren: String,
    linea: String,
    concesion: String,
    fecha: String,
    hora: String,
    tramoCm: String,
    conteo: String,
    conteoConfirmado: String,
    calibre: String,
    observaciones: String,
    estadoActual: String,
    fotoPath: String?,
    fotoUri: Uri?,
    enviar: Boolean,
    onErrores: (List<String>) -> Unit,
    onError: (String) -> Unit,
    onGuardado: () -> Unit
) {
    val metros = tramoCm.trim().toDoubleOrNull()?.div(100.0) ?: 0.0
    val cantidadEstimada = conteo.trim().toIntOrNull()
    val cantidadConfirmada = conteoConfirmado.trim()
        .takeIf { it.isNotEmpty() }
        ?.toIntOrNull() ?: cantidadEstimada
    val medida = calibre.trim().replace(',', '.').toDoubleOrNull()
    val muestra = Muestra(
        id = muestraId,
        centro = centro.trim(),
        tren = tren.trim(),
        linea = linea.trim(),
        concesion = concesion.trim().ifBlank { null },
        fecha = fecha,
        hora = hora,
        tramoMetros = metros,
        operador = usuario.codigo,
        fotoPath = fotoPath,
        cantidadEstimadaML = cantidadEstimada,
        cantidadConfirmada = cantidadConfirmada,
        fuenteConteo = "manual",
        calibrePromedio = medida,
        observaciones = observaciones.trim(),
        estadoRevision = estadoActual
    ).let { actual ->
        if (muestraOriginal == null) actual else actual.copy(
            revisadaPorId = muestraOriginal.revisadaPorId,
            revisadaPor = muestraOriginal.revisadaPor,
            fechaRevision = muestraOriginal.fechaRevision,
            comentarioSupervisor = muestraOriginal.comentarioSupervisor,
            sincronizada = muestraOriginal.sincronizada
        )
    }
    val errores = viewModel.validar(
        muestra = muestra,
        tramoCm = tramoCm,
        conteoEstimado = conteo,
        conteoConfirmado = conteoConfirmado,
        calibre = calibre,
        enviar = enviar,
        fotoAdjunta = fotoUri != null || !fotoPath.isNullOrBlank()
    )
    onErrores(errores)
    onError("")
    if (errores.isNotEmpty()) return

    viewModel.guardar(
        muestra = muestra,
        usuario = usuario,
        fotoUri = fotoUri,
        enviar = enviar,
        alTerminar = onGuardado,
        siHayError = onError
    )
}
