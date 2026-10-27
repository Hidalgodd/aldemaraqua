package com.duoc.aldemaraqua.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import com.duoc.aldemaraqua.ui.theme.AquaHero
import com.duoc.aldemaraqua.ui.theme.AquaPalette
import com.duoc.aldemaraqua.ui.theme.AquaSection
import com.duoc.aldemaraqua.ui.theme.AquaStatTile
import com.duoc.aldemaraqua.ui.theme.AquaBottomBar
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.util.Reglas
import com.duoc.aldemaraqua.viewmodel.ResumenViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenScreen(
    muestras: List<Muestra>,
    usuario: UsuarioDemo,
    viewModel: ResumenViewModel,
    onVolver: () -> Unit,
    onInicio: () -> Unit,
    onHistorial: () -> Unit,
    onNueva: () -> Unit
) {
    val sync by viewModel.sincronizacion.collectAsState()
    val exportacion by viewModel.exportacion.collectAsState()
    val exportadorCsv = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) viewModel.exportarCsv(uri, usuario, muestras)
    }
    val visibles = muestras.filter { Reglas.esVisible(usuario, it) }
    val totalIndividuos = visibles.sumOf { Reglas.conteoEfectivo(it) ?: 0 }
    val densidades = visibles.mapNotNull { muestra ->
        val conteo = Reglas.conteoEfectivo(muestra)
        conteo?.let { Reglas.densidadPorMetro(it, muestra.tramoMetros) }
    }
    val densidadPromedio = densidades.average().takeIf { it.isFinite() } ?: 0.0
    val formatoDensidad = String.format(Locale.getDefault(), "%.1f", densidadPromedio)
    val validadasNoSincronizadas = visibles.count {
        EstadoMuestra.desde(it.estadoRevision) == EstadoMuestra.VALIDADO && !it.sincronizada
    }

    Scaffold(
        containerColor = AquaPalette.Ice,
        bottomBar = { AquaBottomBar(usuario, "resumen", onInicio, onHistorial, {}, onNueva) },
        topBar = {
            TopAppBar(
                title = { Text("Resumen de muestras") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AquaHero("Aldemar aqua / Indicadores", "Resumen operacional",
                "Resultados de las muestras visibles para tu perfil.")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AquaStatTile(visibles.size.toString(), "Muestras", Modifier.weight(1f))
                AquaStatTile(totalIndividuos.toString(), "Individuos", Modifier.weight(1f))
            }
            AquaStatTile("$formatoDensidad ind./m", "Densidad media", Modifier.fillMaxWidth())

            AquaSection("Registros por estado") {
                EstadoMuestra.entries.forEach { estado ->
                    Text("${estado.etiqueta}: ${visibles.count { EstadoMuestra.desde(it.estadoRevision) == estado }}")
                }
            }
            Text("Evolución por centro · tren · línea", style = MaterialTheme.typography.titleLarge,
                color = AquaPalette.Ocean)
            val grupos = visibles
                .filter { EstadoMuestra.desde(it.estadoRevision) == EstadoMuestra.VALIDADO }
                .groupBy { Reglas.claveLinea(it) }
                .toSortedMap(compareBy({ it.first }, { it.second }, { it.third }))
            if (grupos.isEmpty()) {
                Text("Aún no hay muestras validadas para comparar.")
            } else {
                grupos.forEach { (ubicacion, muestrasLinea) ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "${ubicacion.first} · ${ubicacion.second} · ${ubicacion.third}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            muestrasLinea
                                .sortedWith(compareBy({ it.fecha }, { it.hora }, { it.id }))
                                .forEach { muestra ->
                                    val conteo = Reglas.conteoEfectivo(muestra)
                                    val densidad = conteo?.let {
                                        Reglas.densidadPorMetro(it, muestra.tramoMetros)
                                    }
                                    val densidadTexto = densidad?.let {
                                        String.format(Locale.getDefault(), "%.1f", it)
                                    } ?: "Sin dato"
                                    Text(
                                        "${muestra.fecha} · ${conteo ?: "Sin conteo"} individuos · " +
                                            "$densidadTexto individuos/m · " +
                                            EstadoMuestra.desde(muestra.estadoRevision).etiqueta
                                    )
                                }
                        }
                    }
                }
            }

            if (usuario.rol == Rol.ANALISTA) {
                Text("Herramientas del analista", style = MaterialTheme.typography.titleLarge,
                    color = AquaPalette.Ocean)
                Text("Validadas pendientes de sincronizar: $validadasNoSincronizadas")
                Text("El CSV incluye referencias locales a fotos, no las imágenes. La sincronización REST es una simulación académica y no guarda datos en ALDEMAR.")
                Button(
                    onClick = {
                        exportadorCsv.launch("aldemaraqua_muestras_validadas.csv")
                    },
                    enabled = !exportacion.exportando &&
                        visibles.any {
                            EstadoMuestra.desde(it.estadoRevision) == EstadoMuestra.VALIDADO
                        },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (exportacion.exportando) "Exportando..." else "Exportar validadas a CSV")
                }
                exportacion.mensaje?.let {
                    Text(
                        text = it,
                        color = if (exportacion.esError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
                Button(
                    onClick = viewModel::sincronizar,
                    enabled = !sync.sincronizando && validadasNoSincronizadas > 0,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (sync.sincronizando) "Sincronizando..." else "Sincronizar validadas")
                }
                sync.mensaje?.let {
                    Text(
                        text = it,
                        color = if (sync.esError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            } else {
                Text("La exportación CSV y sincronización están disponibles para el perfil analista.")
            }
        }
    }
}

@Composable
private fun ResumenIndicador(etiqueta: String, valor: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(etiqueta, style = MaterialTheme.typography.titleSmall)
            Text(valor, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
