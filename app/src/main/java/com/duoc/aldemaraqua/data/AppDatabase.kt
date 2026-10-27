package com.duoc.aldemaraqua.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Muestra::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun muestraDao(): MuestraDao

    companion object {
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE muestras ADD COLUMN sincronizada INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE muestras ADD COLUMN revisadaPor TEXT")
            }
        }

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE muestras ADD COLUMN concesion TEXT")
            }
        }

        // Conserva los conteos manuales de instalaciones anteriores (antes se guardaban
        // en cantidadEstimadaML, aunque la app no utilizaba inteligencia artificial).
        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE muestras ADD COLUMN conteoManual INTEGER")
                database.execSQL(
                    "UPDATE muestras SET conteoManual = COALESCE(cantidadConfirmada, cantidadEstimadaML) " +
                        "WHERE fuenteConteo = 'manual'"
                )
                database.execSQL(
                    "UPDATE muestras SET cantidadEstimadaML = NULL WHERE fuenteConteo = 'manual'"
                )
            }
        }

        @Volatile
        private var instancia: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aldemaraqua.db"
                ).addMigrations(
                    MIGRACION_1_2,
                    MIGRACION_2_3,
                    MIGRACION_3_4,
                    MIGRACION_4_5
                ).build().also { instancia = it }
            }
        }
    }
}