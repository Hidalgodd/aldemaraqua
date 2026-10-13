package com.duoc.aldemaraqua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import com.duoc.aldemaraqua.data.DatosDemo
import com.duoc.aldemaraqua.data.UsuarioDemo
import com.duoc.aldemaraqua.ui.screen.HomeScreen
import com.duoc.aldemaraqua.ui.screen.LoginScreen
import com.duoc.aldemaraqua.ui.screen.MuestrasScreen
import com.duoc.aldemaraqua.ui.screen.NuevaMuestraScreen
import com.duoc.aldemaraqua.ui.screen.RevisionScreen
import com.duoc.aldemaraqua.ui.screen.ResumenScreen
import com.duoc.aldemaraqua.ui.theme.AldemarAquaTheme
import com.duoc.aldemaraqua.viewmodel.HistorialViewModel
import com.duoc.aldemaraqua.viewmodel.LoginViewModel
import com.duoc.aldemaraqua.viewmodel.NuevaMuestraViewModel
import com.duoc.aldemaraqua.viewmodel.RevisionViewModel
import com.duoc.aldemaraqua.viewmodel.ResumenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AldemarAquaTheme {
                val navController = rememberNavController()
                val loginViewModel: LoginViewModel = viewModel()
                val nuevaMuestraViewModel: NuevaMuestraViewModel = viewModel()
                val historialViewModel: HistorialViewModel = viewModel()
                val revisionViewModel: RevisionViewModel = viewModel()
                val resumenViewModel: ResumenViewModel = viewModel()
                var codigoUsuario by rememberSaveable { mutableStateOf<String?>(null) }
                val usuarioActual = codigoUsuario?.let { codigo ->
                    DatosDemo.usuarios.firstOrNull { it.codigo == codigo }?.usuario()
                }

                NavHost(
                    navController = navController,
                    startDestination = "login",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login") {
                        LoginScreen(viewModel = loginViewModel) { usuario ->
                            codigoUsuario = usuario.codigo
                            navController.navigate("inicio") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("inicio") {
                        val usuario = usuarioActual
                        if (usuario != null) {
                            HomeScreen(
                                usuario = usuario,
                                onNuevaMuestra = { navController.navigate("nueva/0") },
                                onVerHistorial = { navController.navigate("historial") },
                                onVerResumen = { navController.navigate("resumen") },
                                onCerrarSesion = {
                                    codigoUsuario = null
                                    navController.navigate("login") {
                                        popUpTo("inicio") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                    composable(
                        route = "nueva/{muestraId}",
                        arguments = listOf(navArgument("muestraId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val usuario = usuarioActual
                        val muestraId = backStackEntry.arguments?.getLong("muestraId") ?: 0L
                        if (usuario != null) {
                            NuevaMuestraScreen(
                                viewModel = nuevaMuestraViewModel,
                                usuario = usuario,
                                muestraId = muestraId,
                                onGuardado = {
                                    navController.navigate("historial") {
                                        popUpTo("inicio")
                                    }
                                },
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                    composable("historial") {
                        val usuario = usuarioActual
                        if (usuario != null) {
                            MuestrasScreen(
                                viewModel = historialViewModel,
                                usuario = usuario,
                                onEditar = { id -> navController.navigate("nueva/$id") },
                                onRevisar = { id -> navController.navigate("revision/$id") },
                                onResumen = { navController.navigate("resumen") },
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                    composable(
                        route = "revision/{muestraId}",
                        arguments = listOf(navArgument("muestraId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val muestraId = backStackEntry.arguments?.getLong("muestraId") ?: -1L
                        val usuario = usuarioActual
                        if (usuario != null) {
                            RevisionScreen(
                                muestraId = muestraId,
                                usuario = usuario,
                                viewModel = revisionViewModel,
                                onVolver = { navController.popBackStack() },
                                onGuardado = { navController.popBackStack() }
                            )
                        }
                    }
                    composable("resumen") {
                        val usuario = usuarioActual
                        val muestras by historialViewModel.muestras.collectAsState()
                        if (usuario != null) {
                            ResumenScreen(
                                muestras = muestras,
                                usuario = usuario,
                                viewModel = resumenViewModel,
                                onVolver = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}