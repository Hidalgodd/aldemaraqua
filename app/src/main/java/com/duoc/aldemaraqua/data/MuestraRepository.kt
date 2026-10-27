package com.duoc.aldemaraqua.data

import com.duoc.aldemaraqua.util.Reglas
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MuestraRepository(
    private val muestraDao: MuestraDao,
    private val syncApi: SyncApi = SyncApiProvider.api
) {
    val muestras = muestraDao.obtenerTodas()

    suspend fun obtenerPorId(id: Long): Muestra? {
        return muestraDao.obtenerPorId(id)
    }

    suspend fun guardar(muestra: Muestra): Long = muestraDao.insertar(muestra)

    suspend fun actualizar(muestra: Muestra) {
        muestraDao.actualizar(muestra)
    }

    suspend fun eliminarBorrador(muestra: Muestra) {
        muestraDao.eliminar(muestra)
        withContext(Dispatchers.IO) {
            muestra.fotoPath?.let { File(it).delete() }
        }
    }

    suspend fun validadasPendientesDeSincronizar(): List<Muestra> =
        muestraDao.obtenerValidadasPendientesDeSincronizar()

    suspend fun sincronizarValidadas(): Int {
        val pendientes = validadasPendientesDeSincronizar()
        var sincronizadas = 0
        pendientes.forEach { muestra ->
            val respuesta = syncApi.sincronizar(
                MuestraSincronizacion(
                    centro = muestra.centro,
                    concesion = muestra.concesion,
                    tren = muestra.tren,
                    linea = muestra.linea,
                    fecha = muestra.fecha,
                    hora = muestra.hora,
                    tramoMetros = muestra.tramoMetros,
                    operador = muestra.operador,
                    conteo = Reglas.conteoEfectivo(muestra),
                    fotoReferencia = muestra.fotoPath?.let { File(it).name },
                    calibrePromedio = muestra.calibrePromedio,
                    observaciones = muestra.observaciones,
                    estado = muestra.estadoRevision
                )
            )
            if (!respuesta.isSuccessful || respuesta.body()?.id == null) {
                throw java.io.IOException(
                    "La API rechazó una muestra (HTTP ${respuesta.code()}). Se sincronizaron $sincronizadas de ${pendientes.size}."
                )
            }
            muestraDao.actualizar(muestra.copy(sincronizada = true))
            sincronizadas++
        }
        return sincronizadas
    }
}