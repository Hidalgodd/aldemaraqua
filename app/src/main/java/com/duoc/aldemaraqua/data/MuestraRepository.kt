package com.duoc.aldemaraqua.data

class MuestraRepository(private val muestraDao: MuestraDao) {
    val muestras = muestraDao.obtenerTodas()

    suspend fun obtenerPorId(id: Long): Muestra? {
        return muestraDao.obtenerPorId(id)
    }

    suspend fun guardar(muestra: Muestra) {
        muestraDao.insertar(muestra)
    }

    suspend fun actualizar(muestra: Muestra) {
        muestraDao.actualizar(muestra)
    }
}