package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.ui.theme.AquaBottomBar
import com.duoc.aldemaraqua.ui.theme.AquaHero
import com.duoc.aldemaraqua.ui.theme.AquaPalette
import com.duoc.aldemaraqua.ui.theme.AquaSection
import com.duoc.aldemaraqua.ui.theme.AquaStatTile
import com.duoc.aldemaraqua.util.Reglas

@Composable
fun HomeScreen(
    usuario: UsuarioDemo,
    muestras: List<Muestra>,
    onNuevaMuestra: () -> Unit,
    onVerHistorial: () -> Unit,
    onVerResumen: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val visibles = muestras.filter { Reglas.esVisible(usuario, it) }
    val pendientes = visibles.count {
        EstadoMuestra.desde(it.estadoRevision) in listOf(EstadoMuestra.PENDIENTE, EstadoMuestra.CORREGIDO)
    }
    val validadas = visibles.count { EstadoMuestra.desde(it.estadoRevision) == EstadoMuestra.VALIDADO }
    Scaffold(
        containerColor = AquaPalette.Ice,
        bottomBar = {
            AquaBottomBar(
                usuario = usuario, actual = "inicio", onInicio = {},
                onHistorial = onVerHistorial, onResumen = onVerResumen, onNueva = onNuevaMuestra
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            AquaHero(
                eyebrow = "Aldemar aqua  /  Trabajo en terreno",
                title = "Hola, ${usuario.nombre}",
                subtitle = "${usuario.rol.etiqueta} · ${usuario.codigo}"
            )
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AquaStatTile(visibles.size.toString(), "Muestras visibles", Modifier.weight(1f))
                    AquaStatTile(pendientes.toString(), "Por revisar", Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AquaStatTile(validadas.toString(), "Validadas", Modifier.weight(1f))
                    AquaStatTile("Local", "Datos en el dispositivo", Modifier.weight(1f))
                }
                AquaSection("¿Qué quieres hacer hoy?", "Accede a las tareas disponibles para tu perfil.") {
                    if (usuario.rol == Rol.OPERADOR) {
                        Button(onClick = onNuevaMuestra, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                            Text("+  Registrar nueva muestra")
                        }
                    }
                    OutlinedButton(onClick = onVerHistorial, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text(if (usuario.rol == Rol.SUPERVISOR) "Ver muestras por revisar" else "Consultar historial")
                    }
                    OutlinedButton(onClick = onVerResumen, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                        Text("Ver indicadores y evolución")
                    }
                }
                AquaSection("Proceso de seguimiento", "Cada muestra queda vinculada a su centro, tren, línea y fotografía.") {
                    Text("01   Registro en terreno", color = AquaPalette.Ink)
                    Text("02   Revisión del supervisor", color = AquaPalette.Ink)
                    Text("03   Corrección o validación", color = AquaPalette.Ink)
                    Text("04   Consulta y exportación", color = AquaPalette.Ink)
                }
                Text("MVP académico · Los datos de demostración son ficticios.",
                    style = MaterialTheme.typography.bodySmall, color = AquaPalette.Muted)
                TextButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
