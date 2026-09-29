package com.duoc.aldemaraqua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muestras")
data class Muestra(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val centroId: Long,
    val trenId: Long,
    val lineaId: Long,

    val fecha: String,
    val hora: String,
    val tramoMetros: Double,
    val operadorId: Long,

    val fotoPath: String,

    val cantidadEstimadaML: Int? = null,
    val cantidadConfirmada: Int? = null,
    val fuenteConteo: String = "manual",

    val calibrePromedio: Double? = null,
    val calibreMinimo: Double? = null,
    val calibreMaximo: Double? = null,
    val pesoMuestra: Double? = null,
    val observaciones: String = "",

    val estadoRevision: String = "pendiente",
    val revisadaPorId: Long? = null,
    val fechaRevision: String? = null,
    val comentarioSupervisor: String = ""
)