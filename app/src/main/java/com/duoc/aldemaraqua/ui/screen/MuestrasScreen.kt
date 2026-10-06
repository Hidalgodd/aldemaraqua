package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.viewmodel.HistorialViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuestrasScreen(
	viewModel: HistorialViewModel,
	puedeRevisar: Boolean,
	onRevisar: (Long) -> Unit,
	onVolver: () -> Unit
) {
	val muestras by viewModel.muestras.collectAsState()
	var busqueda by remember { mutableStateOf("") }
	var estadoSeleccionado by remember { mutableStateOf("Todos") }
	val estados = listOf("Todos", "Pendiente", "Observado", "Corregido", "Validado")
	val muestrasFiltradas = muestras.filter { muestra ->
		val coincideBusqueda = busqueda.isBlank() ||
			muestra.centro.contains(busqueda, ignoreCase = true) ||
			muestra.tren.contains(busqueda, ignoreCase = true) ||
			muestra.linea.contains(busqueda, ignoreCase = true) ||
			muestra.fecha.contains(busqueda, ignoreCase = true) ||
			muestra.operador.contains(busqueda, ignoreCase = true)
		val coincideEstado = estadoSeleccionado == "Todos" ||
			muestra.estadoRevision.equals(estadoSeleccionado, ignoreCase = true)

		coincideBusqueda && coincideEstado
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Historial de muestras") },
				navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
			)
		}
	) { innerPadding ->
		if (muestras.isEmpty()) {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(24.dp),
				verticalArrangement = Arrangement.Center
			) {
				Text("Todavía no hay muestras registradas.")
			}
		} else {
			Column(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
			) {
				OutlinedTextField(
					value = busqueda,
					onValueChange = { busqueda = it },
					label = { Text("Buscar centro, tren, línea, fecha u operador") },
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = 16.dp, vertical = 8.dp)
				)
				Row(
					modifier = Modifier
					.fillMaxWidth()
					.horizontalScroll(rememberScrollState())
					.padding(horizontal = 16.dp),
					horizontalArrangement = Arrangement.spacedBy(8.dp)
				) {
					estados.forEach { estado ->
						FilterChip(
							selected = estadoSeleccionado == estado,
							onClick = { estadoSeleccionado = estado },
							label = { Text(estado) }
						)
					}
				}
				if (muestrasFiltradas.isEmpty()) {
					Text(
						"No hay muestras que coincidan con la búsqueda.",
						modifier = Modifier.padding(24.dp)
					)
				} else {
					LazyColumn(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f),
						verticalArrangement = Arrangement.spacedBy(10.dp)
					) {
						items(muestrasFiltradas, key = { it.id }) { muestra ->
							MuestraItem(muestra, puedeRevisar) {
								onRevisar(muestra.id)
							}
						}
					}
				}
			}
		}
	}
}

@Composable
private fun MuestraItem(muestra: Muestra, puedeRevisar: Boolean, onRevisar: () -> Unit) {
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
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
					modifier = Modifier
						.fillMaxWidth()
						.height(180.dp)
				)
			}
			Text("${muestra.centro} · ${muestra.tren} · ${muestra.linea}", style = MaterialTheme.typography.titleMedium)
			Text("${muestra.fecha} a las ${muestra.hora}")
			Text("Tramo: ${muestra.tramoMetros} m · Operador: ${muestra.operador}")
			Text("Conteo estimado: ${muestra.cantidadEstimadaML ?: "Sin dato"}")
			Text("Conteo confirmado: ${muestra.cantidadConfirmada ?: "Sin dato"}")
			if (puedeRevisar) {
				Button(onClick = onRevisar, modifier = Modifier.fillMaxWidth()) {
					Text("Revisar muestra")
				}
			}
			if (muestra.observaciones.isNotBlank()) {
				Text(muestra.observaciones)
			}
			Text("Revisión: ${muestra.estadoRevision}", style = MaterialTheme.typography.bodySmall)
		}
	}
}

