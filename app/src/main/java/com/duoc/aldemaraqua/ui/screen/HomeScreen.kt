package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo

private val AzulPrincipal = Color(0xFF0B5C8E)
private val Turquesa = Color(0xFF1FA39A)
private val Fondo = Color(0xFFF4F8FB)
private val Texto = Color(0xFF1B2A38)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	usuario: UsuarioDemo,
	onNuevaMuestra: () -> Unit,
	onVerHistorial: () -> Unit,
	onVerResumen: () -> Unit,
	onCerrarSesion: () -> Unit
) {
	Scaffold(
		containerColor = Fondo,
		topBar = {
			TopAppBar(
				title = { Text("AquaSample", color = Color.White) },
				colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrincipal)
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text("ALDEMAR AQUA", color = Turquesa, style = MaterialTheme.typography.labelLarge)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				"Sesión: ${usuario.nombre} · ${usuario.rol.etiqueta}",
				color = Texto,
				style = MaterialTheme.typography.bodyMedium
			)
			Spacer(modifier = Modifier.height(8.dp))
			Text(
				"Registro de muestras",
				color = Texto,
				style = MaterialTheme.typography.headlineSmall
			)
			Spacer(modifier = Modifier.height(32.dp))
			if (usuario.rol == Rol.OPERADOR) {
				Button(
					onClick = onNuevaMuestra,
					modifier = Modifier.fillMaxWidth(),
					colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal)
				) {
					Text("Nueva muestra")
				}
				Spacer(modifier = Modifier.height(12.dp))
			}
			OutlinedButton(
				onClick = onVerHistorial,
				modifier = Modifier.fillMaxWidth(),
				colors = ButtonDefaults.outlinedButtonColors(contentColor = Turquesa)
			) {
				Text(if (usuario.rol == Rol.SUPERVISOR) "Revisar muestras" else "Ver historial")
			}
			Spacer(modifier = Modifier.height(12.dp))
			if (usuario.rol == Rol.ANALISTA) {
				OutlinedButton(onClick = onVerResumen, modifier = Modifier.fillMaxWidth()) {
					Text("Ver resumen")
				}
				Spacer(modifier = Modifier.height(12.dp))
			}
			OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) {
				Text("Cerrar sesión")
			}
		}
	}
}
