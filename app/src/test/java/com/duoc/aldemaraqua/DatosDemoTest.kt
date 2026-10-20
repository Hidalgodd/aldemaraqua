package com.duoc.aldemaraqua

import com.duoc.aldemaraqua.data.DatosDemo
import com.duoc.aldemaraqua.data.EstadoMuestra
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DatosDemoTest {
    @Test
    fun preparaUnRecorridoCompletoParaLosTresPerfiles() {
        val fechaBase = LocalDate.of(2026, 10, 20)
        val muestras = DatosDemo.muestrasIniciales(fechaBase)

        assertEquals(4, muestras.size)
        assertEquals(
            setOf(
                EstadoMuestra.BORRADOR,
                EstadoMuestra.PENDIENTE,
                EstadoMuestra.OBSERVADO,
                EstadoMuestra.VALIDADO
            ),
            muestras.map { EstadoMuestra.desde(it.estadoRevision) }.toSet()
        )
        assertTrue(muestras.any { it.operador == "OP-01" })
        assertTrue(muestras.any { it.operador == "OP-02" })
        assertTrue(muestras.any { it.estadoRevision == "validado" && it.cantidadConfirmada != null })
        assertTrue(muestras.all { it.fecha <= fechaBase.toString() })
    }
}
