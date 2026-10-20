package com.duoc.aldemaraqua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.duoc.aldemaraqua.data.DatosDemo
import com.duoc.aldemaraqua.data.InicializadorDemo
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
                val context = LocalContext.current.applicationContext
                var demoPreparada by rememberSaveable { mutableStateOf(false) }
                var errorPreparacion by remember { mutableStateOf<String?>(null) }
                var intentoPreparacion by remember { mutableIntStateOf(0) }

                LaunchedEffect(intentoPreparacion) {
                    try {
                        withContext(Dispatchers.IO) {
                            InicializadorDemo(context).prepararSiCorresponde()
                        }
                        errorPreparacion = null
                        demoPreparada = true
                    } catch (error: Exception) {
                        if (error is CancellationException) throw error
                        errorPreparacion = error.message
                            ?: "No se pudieron preparar los datos de demostración."
                    }
                }

                if (!demoPreparada) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (errorPreparacion == null) {
                            CircularProgressIndicator()
                            Text("Preparando la demo…", modifier = Modifier.padding(top = 16.dp))
                        } else {
                            Text(
                                "No se pudo preparar la demo.\n$errorPreparacion",
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            TextButton(onClick = { intentoPreparacion++ }) {
                                Text("Reintentar")
                            }
                        }
                    }
                } else {
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
}