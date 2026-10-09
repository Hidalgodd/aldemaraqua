package com.duoc.aldemaraqua

import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.util.Reglas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReglasTest {
    private val operador = UsuarioDemo("OP-01", "Operador Uno", Rol.OPERADOR)
    private val supervisor = UsuarioDemo("SUP-01", "Supervisor Uno", Rol.SUPERVISOR)
    private val analista = UsuarioDemo("AN-01", "Analista Uno", Rol.ANALISTA)

    private fun muestra(estado: EstadoMuestra, operadorCodigo: String = operador.codigo) =
        Muestra(
            centro = "Centro Bahía Norte",
            tren = "Tren 1",
            linea = "Línea 1",
            fecha = "2026-01-01",
            hora = "10:00",
            tramoMetros = 0.2,
            operador = operadorCodigo,
            concesion = "Concesión 01",
            estadoRevision = estado.name.lowercase()
        )

    @Test
    fun respetaPermisosPorRolYEstado() {
        val borrador = muestra(EstadoMuestra.BORRADOR)
        val pendiente = muestra(EstadoMuestra.PENDIENTE)
        val validada = muestra(EstadoMuestra.VALIDADO)

        assertTrue(Reglas.puedeEditar(operador, borrador))
        assertFalse(Reglas.puedeEditar(operador, pendiente))
        assertFalse(Reglas.puedeEditar(operador, muestra(EstadoMuestra.BORRADOR, "OP-02")))
        assertTrue(Reglas.puedeRevisar(supervisor, pendiente))
        assertFalse(Reglas.puedeRevisar(supervisor, borrador))
        assertTrue(Reglas.esVisible(analista, validada))
        assertFalse(Reglas.esVisible(analista, pendiente))
    }

    @Test
    fun reenvioDeObservadaPasaACorregida() {
        assertEquals(
            EstadoMuestra.CORREGIDO,
            Reglas.estadoAlEnviar(EstadoMuestra.OBSERVADO.name.lowercase())
        )
        assertEquals(
            EstadoMuestra.PENDIENTE,
            Reglas.estadoAlEnviar(EstadoMuestra.BORRADOR.name.lowercase())
        )
    }

    @Test
    fun calculaDensidadYFiltraPorEstado() {
        assertEquals(50.0, Reglas.densidadPorMetro(10, 0.2), 0.001)
        val pendientes = listOf(muestra(EstadoMuestra.PENDIENTE), muestra(EstadoMuestra.VALIDADO))
        assertEquals(
            1,
            Reglas.filtrar(pendientes, "bahía", EstadoMuestra.PENDIENTE).size
        )
        assertEquals(
            1,
            Reglas.filtrar(pendientes, "concesión 01", EstadoMuestra.PENDIENTE).size
        )
    }
}
