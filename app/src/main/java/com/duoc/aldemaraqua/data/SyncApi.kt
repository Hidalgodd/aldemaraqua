package com.duoc.aldemaraqua.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class MuestraSincronizacion(
    val centro: String,
    val concesion: String?,
    val tren: String,
    val linea: String,
    val fecha: String,
    val hora: String,
    val tramoMetros: Double,
    val operador: String,
    val conteo: Int?,
    val calibrePromedio: Double?,
    val observaciones: String,
    val estado: String
)

data class RespuestaSincronizacion(val id: Long?)

interface SyncApi {
    @POST("posts")
    suspend fun sincronizar(@Body muestra: MuestraSincronizacion): Response<RespuestaSincronizacion>
}

object SyncApiProvider {
    val api: SyncApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SyncApi::class.java)
    }
}
