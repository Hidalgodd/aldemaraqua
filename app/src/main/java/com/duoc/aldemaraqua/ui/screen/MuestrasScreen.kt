package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.ui.theme.AquaBottomBar
import com.duoc.aldemaraqua.ui.theme.AquaEstadoChip
import com.duoc.aldemaraqua.ui.theme.AquaHero
import com.duoc.aldemaraqua.ui.theme.AquaPalette
import com.duoc.aldemaraqua.util.Reglas
import com.duoc.aldemaraqua.viewmodel.HistorialViewModel
import java.io.File
import java.util.Locale

@Composable
fun MuestrasScreen(
    viewModel: HistorialViewModel,
    usuario: UsuarioDemo,
    onEditar: (Long) -> Unit,
    onRevisar: (Long) -> Unit,
    onDetalle: (Long) -> Unit,
    onResumen: () -> Unit,
    onInicio: () -> Unit,
    onNueva: () -> Unit,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(usuario) { viewModel.establecerUsuario(usuario) }
    Scaffold(
        containerColor = AquaPalette.Ice,
        bottomBar = { AquaBottomBar(usuario, "historial", onInicio, {}, onResumen, onNueva) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            AquaHero("Aldemar aqua / Registro", "Historial de muestras",
                if (usuario.rol == Rol.SUPERVISOR) "Revisa las muestras pendientes y corregidas."
                else "Consulta la trazabilidad y los resultados de cada muestra.")
            OutlinedTextField(
                value = state.texto, onValueChange = viewModel::onTextoChange,
                placeholder = { Text("Buscar centro, tren, línea, fecha…") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, top = 16.dp, bottom = 6.dp),
                leadingIcon = { Text("⌕") }
            )
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = state.estado == null, onClick = { viewModel.onEstadoChange(null) },
                    label = { Text("Todas") })
                EstadoMuestra.entries.forEach { estado ->
                    FilterChip(selected = state.estado == estado, onClick = { viewModel.onEstadoChange(estado) },
                        label = { Text(estado.etiqueta) })
                }
            }
            Text("${state.muestras.size} muestras encontradas", color = AquaPalette.Muted,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp))
            if (state.cargando) {
                Text("Cargando muestras…", modifier = Modifier.padding(22.dp))
            } else if (state.muestras.isEmpty()) {
                Text("No hay muestras para estos filtros o para tu perfil.",
                    color = AquaPalette.Muted, modifier = Modifier.padding(22.dp))
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 18.dp, end = 18.dp, bottom = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.muestras, key = { it.id }) { muestra ->
                        MuestraTarjeta(muestra, usuario, onClick = { onDetalle(muestra.id) },
                            onEliminar = { viewModel.eliminarBorrador(usuario, muestra) })
                    }
                }
            }
        }
    }
}

@Composable
private fun MuestraTarjeta(muestra: Muestra, usuario: UsuarioDemo, onClick: () -> Unit, onEliminar: () -> Unit) {
    var confirmar by remember(muestra.id) { mutableStateOf(false) }
    if (confirmar) AlertDialog(
        onDismissRequest = { confirmar = false },
        title = { Text("Eliminar borrador") },
        text = { Text("¿Quieres eliminar definitivamente este borrador y su fotografía?") },
        confirmButton = { TextButton(onClick = { confirmar = false; onEliminar() }) { Text("Eliminar") } },
        dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
    )
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AquaPalette.White),
        border = BorderStroke(1.dp, AquaPalette.Border)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                AquaEstadoChip(EstadoMuestra.desde(muestra.estadoRevision))
                Text("#${muestra.id}", style = MaterialTheme.typography.labelMedium, color = AquaPalette.Muted)
            }
            Text(muestra.centro, style = MaterialTheme.typography.titleMedium, color = AquaPalette.Ocean)
            Text("${muestra.tren}   ·   ${muestra.linea}", color = AquaPalette.Ink)
            Text("${muestra.fecha}  ·  ${muestra.hora}  ·  ${muestra.operador}",
                style = MaterialTheme.typography.bodySmall, color = AquaPalette.Muted)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val total = Reglas.conteoEfectivo(muestra)
                Column(Modifier.weight(1f)) {
                    Text("INDIVIDUOS", style = MaterialTheme.typography.labelSmall, color = AquaPalette.Muted)
                    Text(total?.toString() ?: "—", style = MaterialTheme.typography.titleLarge, color = AquaPalette.Ocean)
                }
                Column(Modifier.weight(1f)) {
                    Text("DENSIDAD IND./M", style = MaterialTheme.typography.labelSmall, color = AquaPalette.Muted)
                    Text(total?.let { String.format(Locale.getDefault(), "%.1f", Reglas.densidadPorMetro(it, muestra.tramoMetros)) } ?: "—",
                        style = MaterialTheme.typography.titleLarge, color = AquaPalette.TealDark)
                }
            }
            if (muestra.fotoPath.isNullOrBlank()) {
                Text("Sin fotografía adjunta", style = MaterialTheme.typography.bodySmall, color = AquaPalette.Alert)
            } else {
                Text("● Fotografía asociada", style = MaterialTheme.typography.bodySmall, color = AquaPalette.TealDark)
            }
            if (Reglas.puedeEliminar(usuario, muestra)) {
                TextButton(onClick = { confirmar = true }) { Text("Eliminar borrador") }
            }
            Text("Ver detalle y acciones  ›", style = MaterialTheme.typography.labelLarge, color = AquaPalette.TealDark)
        }
    }
}
