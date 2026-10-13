package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.viewmodel.RevisionViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(
    muestraId: Long,
    usuario: UsuarioDemo,
    viewModel: RevisionViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val muestra by viewModel.muestra.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    var estadoSeleccionado by remember(muestra?.id) {
        mutableStateOf(EstadoMuestra.VALIDADO.name.lowercase())
    }
    var comentario by remember(muestra?.id) {
        mutableStateOf(muestra?.comentarioSupervisor.orEmpty())
    }
    var mensajeError by remember { mutableStateOf("") }
    val estados = listOf(
        EstadoMuestra.OBSERVADO.name.lowercase(),
        EstadoMuestra.VALIDADO.name.lowercase()
    )

    LaunchedEffect(muestraId) {
        viewModel.cargar(muestraId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisión de muestra") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
            )
        }
    ) { innerPadding ->
        when {
            cargando -> Text("Cargando muestra...", modifier = Modifier.padding(innerPadding).padding(24.dp))
            muestra == null -> Text("No se encontró la muestra.", modifier = Modifier.padding(innerPadding).padding(24.dp))
            else -> {
                val muestraActual = muestra!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    muestraActual.fotoPath?.let { rutaFoto ->
                        AsyncImage(
                            model = File(rutaFoto),
                            contentDescription = "Fotografía de la muestra",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                    }
                    Text("${muestraActual.centro} · ${muestraActual.tren} · ${muestraActual.linea}", style = MaterialTheme.typography.titleLarge)
                    muestraActual.concesion?.takeIf { it.isNotBlank() }?.let {
                        Text("Concesión: $it")
                    }
                    Text("Fecha: ${muestraActual.fecha} a las ${muestraActual.hora}")
                    Text("Tramo: ${muestraActual.tramoMetros} m")
                    Text("Operador: ${muestraActual.operador}")
                    Text("Conteo estimado: ${muestraActual.cantidadEstimadaML ?: "Sin dato"}")
                    Text("Conteo confirmado: ${muestraActual.cantidadConfirmada ?: "Sin dato"}")
                    if (muestraActual.observaciones.isNotBlank()) {
                        Text("Observaciones del operador: ${muestraActual.observaciones}")
                    }

                    Text("Resultado de la revisión", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        estados.forEach { estado ->
                            FilterChip(
                                selected = estadoSeleccionado == estado,
                                onClick = { estadoSeleccionado = estado },
                                label = { Text(estado.replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario del supervisor") },
                        supportingText = {
                            if (estadoSeleccionado == EstadoMuestra.OBSERVADO.name.lowercase()) {
                                Text("Obligatorio: explica qué debe corregirse (mínimo 5 caracteres).")
                            }
                        },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (mensajeError.isNotEmpty()) {
                        Text(mensajeError, color = MaterialTheme.colorScheme.error)
                    }
                    Button(
                        onClick = {
                            viewModel.guardarRevision(
                                usuario = usuario,
                                estado = estadoSeleccionado,
                                comentario = comentario,
                                alGuardar = onGuardado,
                                siHayError = { mensajeError = it }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar revisión")
                    }
                }
            }
        }
    }
}