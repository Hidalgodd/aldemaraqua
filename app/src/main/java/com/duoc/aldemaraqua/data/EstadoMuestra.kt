package com.duoc.aldemaraqua.data

enum class EstadoMuestra(val etiqueta: String) {
    BORRADOR("Borrador"),
    PENDIENTE("Pendiente"),
    OBSERVADO("Observado"),
    CORREGIDO("Corregido"),
    VALIDADO("Validado");

    companion object {
        fun desde(valor: String): EstadoMuestra =
            entries.firstOrNull { it.name.equals(valor, ignoreCase = true) }
                ?: BORRADOR
    }
}
