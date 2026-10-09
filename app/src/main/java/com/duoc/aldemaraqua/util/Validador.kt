package com.duoc.aldemaraqua.util

object Validador {
    const val TRAMO_MIN_CM = 1
    const val TRAMO_MAX_CM = 200
    const val CONTEO_MAX = 5000
    const val OBSERVACIONES_MAX = 300

    fun requerido(valor: String, mensaje: String): String? =
        if (valor.isBlank()) mensaje else null

    fun tramoCm(valor: String): String? {
        val centimetros = valor.trim().toIntOrNull()
            ?: return "Ingresa el largo del tramo en cm."
        return if (centimetros !in TRAMO_MIN_CM..TRAMO_MAX_CM) {
            "El tramo debe estar entre $TRAMO_MIN_CM y $TRAMO_MAX_CM cm."
        } else {
            null
        }
    }

    fun conteo(valor: String, obligatorio: Boolean): String? {
        if (valor.isBlank()) {
            return if (obligatorio) "Ingresa el conteo de individuos." else null
        }
        val cantidad = valor.trim().toIntOrNull()
            ?: return "El conteo debe ser un número entero."
        return when {
            cantidad < 0 || cantidad > CONTEO_MAX ->
                "El conteo debe estar entre 0 y $CONTEO_MAX."
            obligatorio && cantidad == 0 ->
                "El conteo debe ser mayor que cero para enviar."
            else -> null
        }
    }

    fun calibre(valor: String): String? {
        if (valor.isBlank()) return null
        val milimetros = valor.trim().replace(',', '.').toDoubleOrNull()
            ?: return "Ingresa un calibre válido."
        return if (!milimetros.isFinite() || milimetros <= 0 || milimetros > 150) {
            "El calibre debe ser mayor que 0 y no superar 150 mm."
        } else {
            null
        }
    }

    fun observaciones(valor: String): String? =
        if (valor.length > OBSERVACIONES_MAX) {
            "Las observaciones no pueden superar $OBSERVACIONES_MAX caracteres."
        } else {
            null
        }

    fun foto(path: String?, obligatoria: Boolean): String? =
        if (obligatoria && path.isNullOrBlank()) {
            "Adjunta una fotografía antes de enviar la muestra."
        } else {
            null
        }

    fun comentarioRevision(valor: String): String? =
        if (valor.trim().length < 5) {
            "Explica la observación (mínimo 5 caracteres)."
        } else {
            null
        }
}
