package com.duoc.aldemaraqua.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.EstadoMuestra

/** Cabecera visual coherente para las pantallas principales. */
@Composable
fun AquaHeader(title: String, description: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
            .background(AquaPalette.Ocean)
            .padding(horizontal = 24.dp, vertical = 26.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Text("ALDEMAR AQUA · TERRENO", color = AquaPalette.Teal,
            style = MaterialTheme.typography.labelLarge)
        Text(title, style = MaterialTheme.typography.headlineMedium,
            color = AquaPalette.White)
        Text(description, style = MaterialTheme.typography.bodyMedium,
            color = AquaPalette.Mint)
    }
}

@Composable
fun AquaMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AquaPalette.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(label, color = AquaPalette.Muted,
                style = MaterialTheme.typography.labelLarge)
            Text(value, color = AquaPalette.Ink,
                style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun AquaPrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = AquaPalette.TealDark),
        shape = RoundedCornerShape(13.dp)
    ) { Text(label, modifier = Modifier.padding(vertical = 6.dp)) }
}

@Composable
fun AquaEstadoChip(estado: EstadoMuestra, modifier: Modifier = Modifier) {
    val (foreground, background) = when (estado) {
        EstadoMuestra.BORRADOR -> AquaPalette.Muted to AquaPalette.Ice
        EstadoMuestra.PENDIENTE -> AquaPalette.OceanLight to androidx.compose.ui.graphics.Color(0xFFFFF2DE)
        EstadoMuestra.OBSERVADO -> AquaPalette.Alert to androidx.compose.ui.graphics.Color(0xFFFFF0EC)
        EstadoMuestra.CORREGIDO -> AquaPalette.OceanLight to androidx.compose.ui.graphics.Color(0xFFE7F1FF)
        EstadoMuestra.VALIDADO -> AquaPalette.TealDark to AquaPalette.Mint
    }
    Row(
        modifier = modifier.background(background, RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(estado.etiqueta, color = foreground, fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelLarge)
    }
}
