package com.duoc.aldemaraqua.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel, onIngreso: (UsuarioDemo) -> Unit) {
	var usuario by remember { mutableStateOf("") }
	var clave by remember { mutableStateOf("") }
	var mensajeError by remember { mutableStateOf("") }

	Scaffold(topBar = { TopAppBar(title = { Text("AquaSample") }) }) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(24.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
			Spacer(modifier = Modifier.height(20.dp))
			OutlinedTextField(
				value = usuario,
				onValueChange = { usuario = it },
				label = { Text("Usuario") },
				singleLine = true,
				modifier = Modifier.fillMaxWidth()
			)
			OutlinedTextField(
				value = clave,
				onValueChange = { clave = it },
				label = { Text("Clave") },
				visualTransformation = PasswordVisualTransformation(),
				keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
				singleLine = true,
				modifier = Modifier.fillMaxWidth()
			)
			if (mensajeError.isNotEmpty()) {
				Text(
					mensajeError,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(top = 8.dp)
				)
			}
			Spacer(modifier = Modifier.height(16.dp))
			Button(
				onClick = {
					val rol = viewModel.autenticar(usuario, clave)
					if (rol == null) {
						mensajeError = "Usuario o clave incorrectos."
					} else {
						mensajeError = ""
						onIngreso(rol)
					}
				},
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Ingresar")
			}
			Spacer(modifier = Modifier.height(12.dp))
			Text("Prueba: OP-01, OP-02, SUP-01 o AN-01 / 1234")
		}
	}
}
