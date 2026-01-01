package com.example.resepku

import java.util.UUID

data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    // Tambahan: Menyimpan ID resource gambar (Int)
    val imageResId: Int
)