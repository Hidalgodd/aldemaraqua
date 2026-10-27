package com.duoc.aldemaraqua.ui.screen

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.ui.theme.AquaHero
import com.duoc.aldemaraqua.ui.theme.AquaPalette
import com.duoc.aldemaraqua.ui.theme.AquaSection
import com.duoc.aldemaraqua.viewmodel.LoginViewModel

@Composable
fun LoginScreen(viewModel: LoginViewModel, onIngreso: (UsuarioDemo) -> Unit) {
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    Scaffold(containerColor = AquaPalette.Ice) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            AquaHero("Aldemar aqua / Acceso seguro", "Bienvenido a AquaField",
                "Tus muestras, fotografías y revisiones en un solo lugar.")
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                AquaSection("Iniciar sesión", "Ingresa con las credenciales de demostración.") {
                    OutlinedTextField(
                        value = usuario, onValueChange = { usuario = it; mensajeError = "" },
                        label = { Text("Código de usuario") }, placeholder = { Text("Ej.: OP-01") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = clave, onValueChange = { clave = it; mensajeError = "" },
                        label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    if (mensajeError.isNotEmpty()) {
                        Text(mensajeError, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = {
                        val perfil = viewModel.autenticar(usuario, clave)
                        if (perfil == null) mensajeError = "Usuario o clave incorrectos."
                        else { mensajeError = ""; onIngreso(perfil) }
                    }, modifier = Modifier.fillMaxWidth().height(52.dp)) { Text("Ingresar a la aplicación") }
                }
                AquaSection("Accesos de prueba", "Para la evaluación académica · contraseña: 1234") {
                    listOf("Operador" to "OP-01", "Supervisor" to "SUP-01", "Analista" to "AN-01").forEach { (rol, codigo) ->
                        OutlinedButton(onClick = { usuario = codigo; clave = "1234"; mensajeError = "" },
                            modifier = Modifier.fillMaxWidth().height(48.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(rol)
                                Text(codigo, color = AquaPalette.TealDark)
                            }
                        }
                    }
                }
                Text("También disponible OP-02 · Sin datos reales de la empresa.",
                    color = AquaPalette.Muted, style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
