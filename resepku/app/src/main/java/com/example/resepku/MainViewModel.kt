package com.example.resepku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.resepku.R

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val database = RecipeDatabase.getDatabase(application)
    private val dao = database.recipeDao()

    val recipes: StateFlow<List<Recipe>> = dao.getAllRecipes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getRecipeById(id: String): Recipe? {
        return dao.getRecipeById(id)
    }

    fun login(username: String, pass: String): Boolean {
        if (username.isNotEmpty() && pass.isNotEmpty()) {
            _isLoggedIn.value = true
            return true
        }
        return false
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun addRecipe(title: String, desc: String, ingredients: String, instructions: String) {
        viewModelScope.launch {
            val newRecipe = Recipe(
                title = title,
                description = desc,
                ingredients = ingredients,
                instructions = instructions,
                imageResId = R.drawable.placeholder_food
            )
            dao.insertRecipe(newRecipe)
        }
    }
}