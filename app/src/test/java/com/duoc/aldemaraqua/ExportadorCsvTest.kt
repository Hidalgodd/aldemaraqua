package com.duoc.aldemaraqua

import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.util.ExportadorCsv
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportadorCsvTest {
    @Test
    fun exportaConcesionYEscapaCamposCsv() {
        val muestra = Muestra(
            centro = "Centro, Norte",
            tren = "Tren 1",
            linea = "Línea \"A\"",
            concesion = "Concesión 01",
            fecha = "2026-10-09",
            hora = "12:30",
            tramoMetros = 0.2,
            operador = "OP-01",
            observaciones = "Primera línea\r\nsegunda línea",
            estadoRevision = "validado"
        )

        val csv = ExportadorCsv.generar(listOf(muestra))

        assertTrue(csv.startsWith("\uFEFF\"id\",\"centro\",\"concesion\""))
        assertTrue(csv.contains("\"Centro, Norte\""))
        assertTrue(csv.contains("\"Línea \"\"A\"\"\""))
        assertTrue(csv.contains("\"Primera línea\r\nsegunda línea\""))
        assertTrue(csv.contains("\"Concesión 01\""))
    }

    @Test
    fun protegeValoresQuePuedenInterpretarseComoFormula() {
        val muestra = Muestra(
            centro = "=HYPERLINK(\"https://example.test\")",
            tren = "Tren 1",
            linea = "Línea 1",
            fecha = "2026-10-09",
            hora = "12:30",
            tramoMetros = 0.2,
            operador = "OP-01"
        )

        val csv = ExportadorCsv.generar(listOf(muestra))

        assertTrue(csv.contains("\"'=HYPERLINK(\"\"https://example.test\"\")\""))
    }
}
