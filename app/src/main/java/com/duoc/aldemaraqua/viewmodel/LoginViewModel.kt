package com.duoc.aldemaraqua.viewmodel

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
	fun autenticar(usuario: String, clave: String): String? {
		if (clave != "1234") return null

		return when (usuario.trim().lowercase()) {
			"operador" -> "operador"
			"supervisor" -> "supervisor"
			else -> null
		}
	}
}