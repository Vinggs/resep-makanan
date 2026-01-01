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

    val categories = listOf("Semua", "Tradisional", "Sarapan", "Makan Siang", "Jajanan", "Diet")

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _allRecipes = dao.getAllRecipes()

    val recipes: StateFlow<List<Recipe>> = combine(_allRecipes, _selectedCategory, _searchQuery) { list, category, query ->
        list.filter { recipe ->
            val matchCategory = if (category == "Semua") true else recipe.category == category

            val matchSearch = if (query.isEmpty()) true else {
                recipe.title.contains(query, ignoreCase = true) ||
                        recipe.ingredients.contains(query, ignoreCase = true)
            }

            matchCategory && matchSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteRecipes: StateFlow<List<Recipe>> = dao.getFavoriteRecipes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
            dao.updateRecipe(updatedRecipe)
        }
    }

    suspend fun getRecipeById(id: String): Recipe? {
        return dao.getRecipeById(id)
    }
}