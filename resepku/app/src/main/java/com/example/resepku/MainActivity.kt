package com.example.resepku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.resepku.ui.theme.ResepKuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResepKuTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()

                // Start Destination langsung ke HOME
                NavHost(navController = navController, startDestination = "home") {

                    // Rute Home
                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onFavoriteClick = { navController.navigate("favorites") }, // Ke halaman favorit
                            onRecipeClick = { recipeId -> navController.navigate("detail/$recipeId") }
                        )
                    }

                    // Rute Favorit (Baru)
                    composable("favorites") {
                        FavoriteScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onRecipeClick = { recipeId -> navController.navigate("detail/$recipeId") }
                        )
                    }

                    // Rute Detail
                    composable(
                        route = "detail/{recipeId}",
                        arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        DetailScreen(
                            recipeId = recipeId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}