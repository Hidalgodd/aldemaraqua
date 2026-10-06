package com.duoc.aldemaraqua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.duoc.aldemaraqua.ui.screen.HomeScreen
import com.duoc.aldemaraqua.ui.screen.LoginScreen
import com.duoc.aldemaraqua.ui.screen.MuestrasScreen
import com.duoc.aldemaraqua.ui.screen.NuevaMuestraScreen
import com.duoc.aldemaraqua.ui.screen.RevisionScreen
import com.duoc.aldemaraqua.ui.theme.AldemarAquaTheme
import com.duoc.aldemaraqua.viewmodel.HistorialViewModel
import com.duoc.aldemaraqua.viewmodel.LoginViewModel
import com.duoc.aldemaraqua.viewmodel.NuevaMuestraViewModel
import com.duoc.aldemaraqua.viewmodel.RevisionViewModel

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
                var rolUsuario by rememberSaveable { mutableStateOf<String?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = "login",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login") {
                        LoginScreen(viewModel = loginViewModel) { rol ->
                            rolUsuario = rol
                            navController.navigate("inicio") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("inicio") {
                        val rol = rolUsuario
                        if (rol != null) {
                            HomeScreen(
                                rolUsuario = rol,
                                onNuevaMuestra = { navController.navigate("nueva") },
                                onVerHistorial = { navController.navigate("historial") },
                                onCerrarSesion = {
                                    rolUsuario = null
                                    navController.navigate("login") {
                                        popUpTo("inicio") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                    composable("nueva") {
                        NuevaMuestraScreen(
                            viewModel = nuevaMuestraViewModel,
                            onGuardado = {
                                navController.navigate("historial") {
                                    popUpTo("inicio")
                                }
                            },
                            onVolver = { navController.popBackStack() }
                        )
                    }
                    composable("historial") {
                        MuestrasScreen(
                            viewModel = historialViewModel,
                            puedeRevisar = rolUsuario == "supervisor",
                            onRevisar = { id -> navController.navigate("revision/$id") },
                            onVolver = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "revision/{muestraId}",
                        arguments = listOf(navArgument("muestraId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val muestraId = backStackEntry.arguments?.getLong("muestraId") ?: -1L
                        RevisionScreen(
                            muestraId = muestraId,
                            viewModel = revisionViewModel,
                            onVolver = { navController.popBackStack() },
                            onGuardado = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}