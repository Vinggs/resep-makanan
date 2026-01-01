package com.example.resepku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = RecipeDatabase.getDatabase(application).recipeDao()

    // 1. Daftar Kategori Statis
    val categories = listOf("Semua", "Tradisional", "Sarapan", "Makan Siang", "Jajanan", "Diet")

    // 2. State Kategori yang sedang dipilih (Default: Semua)
    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // 3. Mengambil Semua Resep dari DB
    private val _allRecipes = dao.getAllRecipes()

    // 4. Logika FILTER: Gabungkan data resep + kategori yang dipilih
    val recipes: StateFlow<List<Recipe>> = combine(_allRecipes, _selectedCategory) { recipes, category ->
        if (category == "Semua") {
            recipes
        } else {
            recipes.filter { it.category == category }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // List Favorit (Tidak terpengaruh filter kategori)
    val favoriteRecipes: StateFlow<List<Recipe>> = dao.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fungsi ganti kategori
    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    suspend fun getRecipeById(id: String): Recipe? {
        return dao.getRecipeById(id)
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
            dao.updateRecipe(updatedRecipe)
        }
    }
}