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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel()

            NavHost(navController = navController, startDestination = "login") {

                // Rute Login
                composable("login") {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // Rute Home
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onAddClick = { navController.navigate("add_recipe") },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        onRecipeClick = { recipeId ->
                            navController.navigate("detail/$recipeId")
                        }
                    )
                }

                // Rute Add Recipe
                composable("add_recipe") {
                    AddRecipeScreen(
                        viewModel = viewModel,
                        onRecipeAdded = { navController.popBackStack() }
                    )
                }

                // Rute Detail (Menerima argumen ID)
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