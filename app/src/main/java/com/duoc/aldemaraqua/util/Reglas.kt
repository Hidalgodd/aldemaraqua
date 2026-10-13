package com.duoc.aldemaraqua.util

import com.duoc.aldemaraqua.data.EstadoMuestra
import com.duoc.aldemaraqua.data.Muestra
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo

object Reglas {
    fun puedeCrear(usuario: UsuarioDemo): Boolean = usuario.rol == Rol.OPERADOR

    fun puedeEditar(usuario: UsuarioDemo, muestra: Muestra): Boolean =
        usuario.rol == Rol.OPERADOR &&
            muestra.operador == usuario.codigo &&
            EstadoMuestra.desde(muestra.estadoRevision) in
            setOf(EstadoMuestra.BORRADOR, EstadoMuestra.OBSERVADO)

    fun puedeRevisar(usuario: UsuarioDemo, muestra: Muestra): Boolean =
        usuario.rol == Rol.SUPERVISOR &&
            EstadoMuestra.desde(muestra.estadoRevision) in
            setOf(EstadoMuestra.PENDIENTE, EstadoMuestra.CORREGIDO)

    fun esVisible(usuario: UsuarioDemo, muestra: Muestra): Boolean =
        when (usuario.rol) {
            Rol.OPERADOR -> muestra.operador == usuario.codigo
            Rol.SUPERVISOR ->
                EstadoMuestra.desde(muestra.estadoRevision) != EstadoMuestra.BORRADOR
            Rol.ANALISTA ->
                EstadoMuestra.desde(muestra.estadoRevision) == EstadoMuestra.VALIDADO
        }

    fun estadoAlEnviar(actual: String): EstadoMuestra =
        if (EstadoMuestra.desde(actual) == EstadoMuestra.OBSERVADO) {
            EstadoMuestra.CORREGIDO
        } else {
            EstadoMuestra.PENDIENTE
        }

    fun densidadPorMetro(conteo: Int, tramoMetros: Double): Double =
        if (tramoMetros <= 0 || !tramoMetros.isFinite()) 0.0 else conteo * 1.0 / tramoMetros

    fun filtrar(
        muestras: List<Muestra>,
        texto: String,
        estado: EstadoMuestra?
    ): List<Muestra> = muestras.filter { muestra ->
        val coincideTexto = texto.isBlank() || listOf(
            muestra.centro,
            muestra.tren,
            muestra.linea,
            muestra.concesion.orEmpty(),
            muestra.operador,
            muestra.fecha
        ).any { it.contains(texto.trim(), ignoreCase = true) }
        coincideTexto && (estado == null || EstadoMuestra.desde(muestra.estadoRevision) == estado)
    }
}
