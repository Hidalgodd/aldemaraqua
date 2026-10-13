package com.duoc.aldemaraqua.util

import com.duoc.aldemaraqua.data.Muestra

object ExportadorCsv {
    private val encabezados = listOf(
        "id",
        "centro",
        "concesion",
        "tren",
        "linea",
        "fecha",
        "hora",
        "tramo_cm",
        "operador",
        "conteo_estimado",
        "conteo_confirmado",
        "calibre_promedio_mm",
        "calibre_minimo_mm",
        "calibre_maximo_mm",
        "peso_muestra",
        "observaciones",
        "estado_revision",
        "comentario_supervisor",
        "fecha_revision"
    )

    fun generar(muestras: List<Muestra>): String = buildString {
        append('\uFEFF')
        append(encabezados.joinToString(",") { it.csvSeguro() })
        append("\r\n")
        muestras.forEach { muestra ->
            listOf(
                muestra.id.toString(),
                muestra.centro,
                muestra.concesion.orEmpty(),
                muestra.tren,
                muestra.linea,
                muestra.fecha,
                muestra.hora,
                (muestra.tramoMetros * 100).toString(),
                muestra.operador,
                muestra.cantidadEstimadaML?.toString().orEmpty(),
                muestra.cantidadConfirmada?.toString().orEmpty(),
                muestra.calibrePromedio?.toString().orEmpty(),
                muestra.calibreMinimo?.toString().orEmpty(),
                muestra.calibreMaximo?.toString().orEmpty(),
                muestra.pesoMuestra?.toString().orEmpty(),
                muestra.observaciones,
                muestra.estadoRevision,
                muestra.comentarioSupervisor,
                muestra.fechaRevision.orEmpty()
            ).joinTo(this, separator = ",") { it.csvSeguro() }
            append("\r\n")
        }
    }

    private fun String.csvSeguro(): String {
        val formulaCandidate = trimStart()
        val safeValue = if (
            formulaCandidate.startsWith('=') ||
            formulaCandidate.startsWith('+') ||
            formulaCandidate.startsWith('-') ||
            formulaCandidate.startsWith('@')
        ) {
            "'$this"
        } else {
            this
        }
        return "\"${safeValue.replace("\"", "\"\"")}\""
    }
}
