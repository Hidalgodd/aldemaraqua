package com.duoc.aldemaraqua.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreFicticio: String,
    val rol: String // "operador", "supervisor", "analista", "cliente", "administrador"
)