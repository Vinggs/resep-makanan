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

                NavHost(navController = navController, startDestination = "home") {

                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onFavoriteClick = { navController.navigate("favorites") },
                            onRecipeClick = { recipeId -> navController.navigate("detail/$recipeId") }
                        )
                    }

                    composable("favorites") {
                        FavoriteScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onRecipeClick = { recipeId -> navController.navigate("detail/$recipeId") }
                        )
                    }

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