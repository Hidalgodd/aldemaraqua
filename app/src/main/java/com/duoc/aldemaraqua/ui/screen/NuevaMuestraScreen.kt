package com.duoc.aldemaraqua.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.viewmodel.NuevaMuestraViewModel
import android.net.Uri
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private fun fechaEsValida(fecha: String): Boolean {
    return try {
        LocalDate.parse(fecha)
        true
    } catch (_: DateTimeParseException) {
        false
    }
}

private fun horaEsValida(hora: String): Boolean {
    return try {
        LocalTime.parse(hora, DateTimeFormatter.ofPattern("HH:mm"))
        true
    } catch (_: DateTimeParseException) {
        false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaMuestraScreen(
    viewModel: NuevaMuestraViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    var centro by remember { mutableStateOf("") }
    var tren by remember { mutableStateOf("") }
    var linea by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(LocalDate.now().toString()) }
    var hora by remember {
        mutableStateOf(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    }
    var tramoMetros by remember { mutableStateOf("") }
    var operador by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var conteoEstimadoTexto by remember { mutableStateOf("") }
    var conteoConfirmadoTexto by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var fotoSeleccionada by remember { mutableStateOf<Uri?>(null) }
    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> fotoSeleccionada = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva muestra") },
                navigationIcon = { TextButton(onClick = onVolver) { Text("Volver") } }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(centro, { centro = it }, label = { Text("Centro") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(tren, { tren = it }, label = { Text("Tren") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(linea, { linea = it }, label = { Text("Línea") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(fecha, { fecha = it }, label = { Text("Fecha (AAAA-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(hora, { hora = it }, label = { Text("Hora (HH:MM)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = tramoMetros,
                onValueChange = { tramoMetros = it },
                label = { Text("Tramo en metros") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(operador, { operador = it }, label = { Text("Operador") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Conteo (simulación)",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            Text("El módulo de conteo automático aún no está conectado. Ingresa una estimación de prueba.")
            OutlinedTextField(
                value = conteoEstimadoTexto,
                onValueChange = { conteoEstimadoTexto = it },
                label = { Text("Cantidad estimada de choritos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = conteoConfirmadoTexto,
                onValueChange = { conteoConfirmadoTexto = it },
                label = { Text("Cantidad confirmada o corregida") },
                supportingText = { Text("Déjalo vacío para aceptar la estimación.") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { selectorFoto.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (fotoSeleccionada == null) "Seleccionar foto" else "Cambiar foto")
            }
            fotoSeleccionada?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Foto seleccionada para la muestra",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            if (mensajeError.isNotEmpty()) {
                Text(mensajeError, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val metros = tramoMetros.trim().replace(',', '.').toDoubleOrNull()
                    val conteoEstimado = conteoEstimadoTexto.trim().toIntOrNull()
                    val conteoConfirmado = if (conteoConfirmadoTexto.isBlank()) {
                        conteoEstimado
                    } else {
                        conteoConfirmadoTexto.trim().toIntOrNull()
                    }
                    if (centro.isBlank() || tren.isBlank() || linea.isBlank() || operador.isBlank()) {
                        mensajeError = "Completa centro, tren, línea y operador."
                    } else if (!fechaEsValida(fecha.trim())) {
                        mensajeError = "Ingresa una fecha válida con formato AAAA-MM-DD."
                    } else if (!horaEsValida(hora.trim())) {
                        mensajeError = "Ingresa una hora válida con formato HH:MM."
                    } else if (metros == null || metros <= 0) {
                        mensajeError = "Ingresa un tramo válido mayor que cero."
                    } else if (conteoEstimado == null || conteoEstimado < 0) {
                        mensajeError = "Ingresa una estimación de conteo válida (cero o más)."
                    } else if (conteoConfirmado == null || conteoConfirmado < 0) {
                        mensajeError = "El conteo confirmado debe ser un número entero igual o mayor que cero."
                    } else {
                        mensajeError = ""
                        viewModel.guardar(
                            Muestra(
                                centro = centro.trim(),
                                tren = tren.trim(),
                                linea = linea.trim(),
                                fecha = fecha.trim(),
                                hora = hora.trim(),
                                tramoMetros = metros,
                                operador = operador.trim(),
                                observaciones = observaciones.trim(),
                                cantidadEstimadaML = conteoEstimado,
                                cantidadConfirmada = conteoConfirmado,
                                fuenteConteo = "simulado"
                            ),
                            fotoSeleccionada,
                            onGuardado,
                            { error -> mensajeError = error }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar muestra")
            }
        }
    }
}