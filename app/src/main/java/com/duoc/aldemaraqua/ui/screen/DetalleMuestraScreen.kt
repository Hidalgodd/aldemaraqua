package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.ui.theme.AquaEstadoChip
import com.duoc.aldemaraqua.ui.theme.AquaHero
import com.duoc.aldemaraqua.ui.theme.AquaLine
import com.duoc.aldemaraqua.ui.theme.AquaPalette
import com.duoc.aldemaraqua.ui.theme.AquaSection
import com.duoc.aldemaraqua.util.Reglas
import java.io.File
import java.util.Locale

@Composable
fun DetalleMuestraScreen(
    muestra: Muestra?, usuario: UsuarioDemo,
    onEditar: () -> Unit, onRevisar: () -> Unit, onVolver: () -> Unit
) {
    Scaffold(containerColor = AquaPalette.Ice) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            AquaHero("Aldemar aqua / Trazabilidad", "Detalle de muestra",
                "Identificación, fotografía, conteo y revisión en un solo registro.")
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                TextButton(onClick = onVolver) { Text("‹ Volver al historial") }
                if (muestra == null || !Reglas.esVisible(usuario, muestra)) {
                    Text("La muestra no está disponible para este usuario.")
                } else {
                    AquaSection("Muestra #${muestra.id}") {
                        AquaEstadoChip(EstadoMuestra.desde(muestra.estadoRevision))
                        AquaLine("Ubicación", "${muestra.centro} · ${muestra.tren} · ${muestra.linea}")
                        muestra.concesion?.takeIf { it.isNotBlank() }?.let { AquaLine("Concesión", it) }
                        AquaLine("Fecha y operador", "${muestra.fecha} ${muestra.hora} · ${muestra.operador}")
                        AquaLine("Tramo muestreado", "${(muestra.tramoMetros * 100).toInt()} cm")
                    }
                    AquaSection("Fotografía de la muestra", "Evidencia asociada al registro") {
                        if (muestra.fotoPath.isNullOrBlank()) {
                            Text("Todavía no hay fotografía.", color = AquaPalette.Alert)
                        } else {
                            if (File(muestra.fotoPath).name.startsWith("demo_sintetica_")) {
                                Text("Imagen sintética de demostración", color = AquaPalette.Muted,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                            AsyncImage(model = File(muestra.fotoPath),
                                contentDescription = "Evidencia fotográfica de la muestra",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(220.dp))
                        }
                    }
                    AquaSection("Resultados") {
                        AquaLine("Conteo manual", Reglas.conteoRegistrado(muestra)?.toString() ?: "Sin dato")
                        AquaLine("Conteo confirmado", Reglas.conteoEfectivo(muestra)?.toString() ?: "Sin dato")
                        AquaLine("Densidad", Reglas.conteoEfectivo(muestra)?.let {
                            "${String.format(Locale.getDefault(), "%.1f", Reglas.densidadPorMetro(it, muestra.tramoMetros))} individuos/m"
                        } ?: "Sin dato")
                        muestra.calibrePromedio?.let { AquaLine("Calibre promedio", "$it mm") }
                    }
                    if (muestra.observaciones.isNotBlank() || muestra.comentarioSupervisor.isNotBlank()) {
                        AquaSection("Observaciones") {
                            if (muestra.observaciones.isNotBlank()) AquaLine("Operador", muestra.observaciones)
                            if (muestra.comentarioSupervisor.isNotBlank()) AquaLine("Supervisor", muestra.comentarioSupervisor)
                        }
                    }
                    if (Reglas.puedeEditar(usuario, muestra)) {
                        Button(onClick = onEditar, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                            Text(if (EstadoMuestra.desde(muestra.estadoRevision) == EstadoMuestra.OBSERVADO)
                                "Corregir y reenviar" else "Editar borrador")
                        }
                    }
                    if (usuario.rol == Rol.SUPERVISOR && Reglas.puedeRevisar(usuario, muestra)) {
                        Button(onClick = onRevisar, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                            Text("Revisar muestra")
                        }
                    }
                }
            }
        }
    }
}
