package com.duoc.aldemaraqua.viewmodel

import androidx.lifecycle.ViewModel
import com.duoc.aldemaraqua.data.DatosDemo
import com.duoc.aldemaraqua.data.Rol
import com.duoc.aldemaraqua.data.UsuarioDemo

class LoginViewModel : ViewModel() {
    fun autenticar(usuario: String, clave: String): UsuarioDemo? {
        val identificador = usuario.trim()
        val credencial = DatosDemo.usuarios.firstOrNull {
            it.codigo.equals(identificador, ignoreCase = true) && it.clave == clave
        }
        if (credencial != null) return credencial.usuario()

        return when (identificador.lowercase()) {
            "operador" -> DatosDemo.usuarios.first {
                it.rol == Rol.OPERADOR
            }.takeIf { it.clave == clave }?.usuario()
            "supervisor" -> DatosDemo.usuarios.first {
                it.rol == Rol.SUPERVISOR
            }.takeIf { it.clave == clave }?.usuario()
            "analista" -> DatosDemo.usuarios.first {
                it.rol == Rol.ANALISTA
            }.takeIf { it.clave == clave }?.usuario()
            else -> null
        }
    }
}