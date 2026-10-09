package com.duoc.aldemaraqua.data

enum class Rol(val etiqueta: String) {
    OPERADOR("Operador de muestreo"),
    SUPERVISOR("Supervisor técnico"),
    ANALISTA("Analista de reportes")
}

data class UsuarioDemo(
    val codigo: String,
    val nombre: String,
    val rol: Rol
)

data class CredencialDemo(
    val codigo: String,
    val nombre: String,
    val clave: String,
    val rol: Rol
) {
    fun usuario() = UsuarioDemo(codigo, nombre, rol)
}

object DatosDemo {
    val usuarios = listOf(
        CredencialDemo("OP-01", "Operador Uno", "1234", Rol.OPERADOR),
        CredencialDemo("OP-02", "Operador Dos", "1234", Rol.OPERADOR),
        CredencialDemo("SUP-01", "Supervisor Uno", "1234", Rol.SUPERVISOR),
        CredencialDemo("AN-01", "Analista Uno", "1234", Rol.ANALISTA)
    )

    val centros = listOf("Centro Bahía Norte", "Centro Isla Sur", "Centro Estero Azul")
    val trenes = (1..4).map { "Tren $it" }
    val lineas = (1..10).map { "Línea $it" }
}
