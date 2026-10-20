package com.duoc.aldemaraqua.data

import java.time.LocalDate

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

    fun muestrasIniciales(fechaBase: LocalDate = LocalDate.now()) = listOf(
        Muestra(
            centro = centros[0],
            tren = trenes[0],
            linea = lineas[0],
            concesion = "Concesión Norte 01",
            fecha = fechaBase.minusDays(1).toString(),
            hora = "09:15",
            tramoMetros = 0.2,
            operador = "OP-01",
            cantidadEstimadaML = 146,
            calibrePromedio = 62.4,
            observaciones = "Muestra de demostración pendiente de revisión.",
            estadoRevision = EstadoMuestra.PENDIENTE.name.lowercase()
        ),
        Muestra(
            centro = centros[1],
            tren = trenes[1],
            linea = lineas[2],
            concesion = "Concesión Isla 03",
            fecha = fechaBase.minusDays(2).toString(),
            hora = "11:40",
            tramoMetros = 0.2,
            operador = "OP-02",
            cantidadEstimadaML = 132,
            cantidadConfirmada = 128,
            calibrePromedio = 59.8,
            observaciones = "Muestra de demostración validada.",
            estadoRevision = EstadoMuestra.VALIDADO.name.lowercase(),
            revisadaPor = "SUP-01",
            fechaRevision = fechaBase.minusDays(1).atTime(14, 10).toString()
        ),
        Muestra(
            centro = centros[2],
            tren = trenes[0],
            linea = lineas[4],
            concesion = "Concesión Estero 05",
            fecha = fechaBase.minusDays(1).toString(),
            hora = "15:05",
            tramoMetros = 0.25,
            operador = "OP-02",
            cantidadEstimadaML = 175,
            observaciones = "Muestra de demostración observada.",
            estadoRevision = EstadoMuestra.OBSERVADO.name.lowercase(),
            revisadaPor = "SUP-01",
            fechaRevision = fechaBase.minusDays(1).atTime(16, 20).toString(),
            comentarioSupervisor = "Revisar el conteo antes de reenviar."
        ),
        Muestra(
            centro = centros[0],
            tren = trenes[2],
            linea = lineas[1],
            concesion = "Concesión Norte 02",
            fecha = fechaBase.toString(),
            hora = "08:30",
            tramoMetros = 0.2,
            operador = "OP-01",
            cantidadEstimadaML = 120,
            observaciones = "Borrador de demostración del operador.",
            estadoRevision = EstadoMuestra.BORRADOR.name.lowercase()
        )
    )
}
