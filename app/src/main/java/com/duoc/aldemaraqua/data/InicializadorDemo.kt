package com.duoc.aldemaraqua.data

import android.content.Context
import java.io.IOException

class InicializadorDemo(context: Context) {
    private val appContext = context.applicationContext

    suspend fun prepararSiCorresponde() {
        val preferencias = appContext.getSharedPreferences("aldemaraqua_demo", Context.MODE_PRIVATE)
        val claveInicializada = "muestras_iniciales_v1"
        if (preferencias.getBoolean(claveInicializada, false)) return

        val dao = AppDatabase.obtenerInstancia(appContext).muestraDao()
        if (dao.contarTodas() == 0) {
            dao.insertarTodas(DatosDemo.muestrasIniciales())
        }
        if (!preferencias.edit().putBoolean(claveInicializada, true).commit()) {
            throw IOException("No se pudo guardar el estado de preparación de la demo.")
        }
    }
}
