package com.duoc.aldemaraqua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MuestraDao {
    @Query("SELECT * FROM muestras ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<Muestra>>

    @Query("SELECT * FROM muestras WHERE id = :id")
    suspend fun obtenerPorId(id: Long): Muestra?

    @Insert
    suspend fun insertar(muestra: Muestra): Long

    @Update
    suspend fun actualizar(muestra: Muestra)

    @Query("SELECT * FROM muestras WHERE estadoRevision = 'validado' AND sincronizada = 0")
    suspend fun obtenerValidadasPendientesDeSincronizar(): List<Muestra>
}