package com.duoc.aldemaraqua.data

import android.content.Context
import java.io.IOException
import java.io.File

class InicializadorDemo(context: Context) {
    private val appContext = context.applicationContext

    suspend fun prepararSiCorresponde() {
        val preferencias = appContext.getSharedPreferences("aldemaraqua_demo", Context.MODE_PRIVATE)
        val claveInicializada = "muestras_iniciales_v1"
        if (preferencias.getBoolean(claveInicializada, false)) return

        val dao = AppDatabase.obtenerInstancia(appContext).muestraDao()
        if (dao.contarTodas() == 0) {
            // Imágenes SINTÉTICAS, rotuladas como ficticias. No son evidencia de campo.
            val carpeta = File(appContext.filesDir, "fotos_muestras").apply { mkdirs() }
            val demo = DatosDemo.muestrasIniciales().mapIndexed { indice, muestra ->
                if (muestra.estadoRevision == EstadoMuestra.BORRADOR.name.lowercase()) {
                    muestra
                } else {
                    val archivo = File(carpeta, "demo_sintetica_${indice + 1}.png")
                    appContext.assets.open("muestra_sintetica.png").use { origen ->
                        archivo.outputStream().use { destino -> origen.copyTo(destino) }
                    }
                    muestra.copy(fotoPath = archivo.absolutePath)
                }
            }
            dao.insertarTodas(demo)
        }
        if (!preferencias.edit().putBoolean(claveInicializada, true).commit()) {
            throw IOException("No se pudo guardar el estado de preparación de la demo.")
        }
    }
}
