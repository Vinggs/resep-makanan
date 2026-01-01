package com.example.resepku

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val imageResId: Int
)