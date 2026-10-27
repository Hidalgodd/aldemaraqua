package com.duoc.aldemaraqua

import com.duoc.aldemaraqua.util.Validador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorTest {
    @Test
    fun aceptaLimitesDelTramo() {
        assertNull(Validador.tramoCm("1"))
        assertNull(Validador.tramoCm("200"))
        assertEquals(
            "El tramo debe estar entre 1 y 200 cm.",
            Validador.tramoCm("201")
        )
    }

    @Test
    fun conteoEsOpcionalEnBorradorPeroObligatorioAlEnviar() {
        assertNull(Validador.conteo("", obligatorio = false))
        assertEquals(
            "Ingresa el conteo de individuos.",
            Validador.conteo("", obligatorio = true)
        )
        assertEquals(
            "El conteo debe ser mayor que cero para enviar.",
            Validador.conteo("0", obligatorio = true)
        )
        assertNull(Validador.conteo("12", obligatorio = true))
    }

    @Test
    fun validaCalibreObservacionesFotoYComentario() {
        assertNull(Validador.calibre("12,5"))
        assertEquals("Ingresa un calibre válido.", Validador.calibre("abc"))
        assertEquals(
            "Las observaciones no pueden superar 300 caracteres.",
            Validador.observaciones("x".repeat(301))
        )
        assertEquals(
            "Adjunta una fotografía antes de enviar la muestra.",
            Validador.foto(null, obligatoria = true)
        )
        assertEquals(
            "Explica la observación (mínimo 5 caracteres).",
            Validador.comentarioRevision("no")
        )
    }
}
