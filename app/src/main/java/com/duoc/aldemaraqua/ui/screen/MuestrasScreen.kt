package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.util.Reglas
import com.duoc.aldemaraqua.viewmodel.HistorialViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuestrasScreen(
    viewModel: HistorialViewModel,
    usuario: UsuarioDemo,
    onEditar: (Long) -> Unit,
    onRevisar: (Long) -> Unit,
    onResumen: () -> Unit,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(usuario) { viewModel.establecerUsuario(usuario) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de muestras") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } },
                actions = { TextButton(onClick = onResumen) { Text("Resumen") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = state.texto,
                onValueChange = viewModel::onTextoChange,
                label = { Text("Buscar centro, concesión, tren, línea, fecha u operador") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.estado == null,
                    onClick = { viewModel.onEstadoChange(null) },
                    label = { Text("Todos") }
                )
                EstadoMuestra.entries.forEach { estado ->
                    FilterChip(
                        selected = state.estado == estado,
                        onClick = { viewModel.onEstadoChange(estado) },
                        label = { Text(estado.etiqueta) }
                    )
                }
            }
            if (state.cargando) {
                Text("Cargando muestras...", modifier = Modifier.padding(24.dp))
            } else if (state.muestras.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (state.texto.isBlank() && state.estado == null) {
                            "No hay muestras disponibles para tu perfil."
                        } else {
                            "No hay muestras que coincidan con la búsqueda."
                        }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.muestras, key = { it.id }) { muestra ->
                        MuestraItem(
                            muestra = muestra,
                            usuario = usuario,
                            onEditar = { onEditar(muestra.id) },
                            onRevisar = { onRevisar(muestra.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MuestraItem(
    muestra: Muestra,
    usuario: UsuarioDemo,
    onEditar: () -> Unit,
    onRevisar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            muestra.fotoPath?.let { rutaFoto ->
                AsyncImage(
                    model = File(rutaFoto),
                    contentDescription = "Foto de la muestra",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            }
            Text(
                "${muestra.centro} · ${muestra.tren} · ${muestra.linea}",
                style = MaterialTheme.typography.titleMedium
            )
            muestra.concesion?.takeIf { it.isNotBlank() }?.let {
                Text("Concesión: $it")
            }
            Text("${muestra.fecha} a las ${muestra.hora}")
            Text("Tramo: ${muestra.tramoMetros * 100} cm · Operador: ${muestra.operador}")
            Text("Conteo estimado: ${muestra.cantidadEstimadaML ?: "Sin dato"}")
            Text("Conteo confirmado: ${muestra.cantidadConfirmada ?: "Sin dato"}")
            Text("Estado: ${EstadoMuestra.desde(muestra.estadoRevision).etiqueta}")
            if (muestra.comentarioSupervisor.isNotBlank()) {
                Text("Comentario: ${muestra.comentarioSupervisor}")
            }
            if (muestra.observaciones.isNotBlank()) Text(muestra.observaciones)

            when {
                Reglas.puedeEditar(usuario, muestra) -> {
                    Button(onClick = onEditar, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            if (EstadoMuestra.desde(muestra.estadoRevision) == EstadoMuestra.OBSERVADO) {
                                "Corregir y reenviar"
                            } else {
                                "Editar borrador"
                            }
                        )
                    }
                }
                usuario.rol == Rol.SUPERVISOR && Reglas.puedeRevisar(usuario, muestra) -> {
                    Button(onClick = onRevisar, modifier = Modifier.fillMaxWidth()) {
                        Text("Revisar muestra")
                    }
                }
            }
        }
    }
}
