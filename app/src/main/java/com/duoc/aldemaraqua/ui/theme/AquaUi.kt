package com.duoc.aldemaraqua.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo

/** Componentes reutilizables del diseño AquaField. Todos los callbacks permanecen en las pantallas. */
@Composable
fun AquaHero(eyebrow: String, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(AquaPalette.Ocean, AquaPalette.OceanLight)),
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
    ) {
        Column(
            modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 24.dp, bottom = 26.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(eyebrow.uppercase(), color = AquaPalette.Teal, style = MaterialTheme.typography.labelLarge)
            Text(title, color = AquaPalette.White, style = MaterialTheme.typography.headlineMedium)
            Text(subtitle, color = AquaPalette.Mint, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun AquaSection(title: String, subtitle: String? = null, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AquaPalette.White),
        shape = RoundedCornerShape(19.dp),
        border = BorderStroke(1.dp, AquaPalette.Border)
    ) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, color = AquaPalette.Ocean, style = MaterialTheme.typography.titleMedium)
            if (subtitle != null) Text(subtitle, color = AquaPalette.Muted, style = MaterialTheme.typography.bodySmall)
            content()
        }
    }
}

@Composable
fun AquaLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = AquaPalette.Muted, style = MaterialTheme.typography.labelMedium)
        Text(value, color = AquaPalette.Ink, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AquaStatusPill(label: String, positive: Boolean = true) {
    Surface(
        color = if (positive) AquaPalette.Mint else androidx.compose.ui.graphics.Color(0xFFFFF2DE),
        shape = CircleShape
    ) {
        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = if (positive) AquaPalette.TealDark else AquaPalette.OceanLight,
            style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun AquaStatTile(value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(17.dp), colors = CardDefaults.cardColors(containerColor = AquaPalette.White),
        border = BorderStroke(1.dp, AquaPalette.Border)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold,
                color = AquaPalette.Ocean, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, style = MaterialTheme.typography.bodySmall, color = AquaPalette.Muted)
        }
    }
}

@Composable
fun AquaBottomBar(
    usuario: UsuarioDemo,
    actual: String,
    onInicio: () -> Unit,
    onHistorial: () -> Unit,
    onResumen: () -> Unit,
    onNueva: () -> Unit
) {
    NavigationBar(containerColor = AquaPalette.White, tonalElevation = 3.dp) {
        val colores = NavigationBarItemDefaults.colors(
            selectedIconColor = AquaPalette.TealDark, selectedTextColor = AquaPalette.TealDark,
            indicatorColor = AquaPalette.Mint, unselectedIconColor = AquaPalette.Muted,
            unselectedTextColor = AquaPalette.Muted
        )
        NavigationBarItem(selected = actual == "inicio", onClick = onInicio,
            icon = { Text("⌂", style = MaterialTheme.typography.titleLarge) },
            label = { Text("Inicio") }, colors = colores)
        NavigationBarItem(selected = actual == "historial", onClick = onHistorial,
            icon = { Text("☷", style = MaterialTheme.typography.titleLarge) },
            label = { Text(if (usuario.rol == Rol.SUPERVISOR) "Revisar" else "Historial") }, colors = colores)
        if (usuario.rol == Rol.OPERADOR) {
            NavigationBarItem(selected = actual == "nueva", onClick = onNueva,
                icon = { Text("+", style = MaterialTheme.typography.titleLarge) },
                label = { Text("Nueva") }, colors = colores)
        }
        NavigationBarItem(selected = actual == "resumen", onClick = onResumen,
            icon = { Text("▥", style = MaterialTheme.typography.titleLarge) },
            label = { Text("Resumen") }, colors = colores)
    }
}
