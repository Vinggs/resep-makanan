package com.example.resepku

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// Pastikan import R sesuai package Anda
import com.example.resepku.R

@Database(entities = [Recipe::class], version = 2, exportSchema = false) // Naikkan version jadi 2 (atau uninstall app)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "resepku_database"
                )
                    .fallbackToDestructiveMigration() // Hapus data lama jika versi berubah
                    .addCallback(RecipeDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RecipeDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(context)
            }
        }

        suspend fun populateDatabase(context: Context) {
            val dao = getDatabase(context).recipeDao()

            // DATA AWAL DENGAN KATEGORI
            val initialRecipes = listOf(
                Recipe(
                    title = "Nasi Goreng",
                    description = "Nasi goreng spesial pedas.",
                    ingredients = "Nasi, Bawang, Cabai, Telur",
                    instructions = "Tumis bumbu, masukkan nasi.",
                    imageResId = R.drawable.nasi_goreng,
                    category = "Sarapan"
                ),
                Recipe(
                    title = "Sate Ayam",
                    description = "Sate ayam bumbu kacang.",
                    ingredients = "Ayam, Kacang, Kecap",
                    instructions = "Bakar ayam, sajikan bumbu.",
                    imageResId = R.drawable.sate_ayam,
                    category = "Tradisional"
                ),
                Recipe(
                    title = "Rendang",
                    description = "Daging sapi santan.",
                    ingredients = "Daging, Santan",
                    instructions = "Masak lama.",
                    imageResId = R.drawable.rendang,
                    category = "Tradisional"
                ),
                Recipe(
                    title = "Soto Betawi",
                    description = "Soto kuah susu.",
                    ingredients = "Daging, Susu",
                    instructions = "Rebus kuah.",
                    imageResId = R.drawable.soto,
                    category = "Makan Siang"
                ),
                Recipe(
                    title = "Gado-gado",
                    description = "Salad sayur.",
                    ingredients = "Sayur, Kacang",
                    instructions = "Campur semua.",
                    imageResId = R.drawable.gado_gado,
                    category = "Diet"
                ),
                Recipe(
                    title = "Bakso Sapi",
                    description = "Bakso kuah segar.",
                    ingredients = "Bakso, Mie",
                    instructions = "Rebus bakso.",
                    imageResId = R.drawable.bakso,
                    category = "Jajanan"
                ),
                Recipe(
                    title = "Mie Ayam",
                    description = "Mie topping ayam.",
                    ingredients = "Mie, Ayam",
                    instructions = "Rebus mie.",
                    imageResId = R.drawable.mie_ayam,
                    category = "Jajanan"
                )
            )
            dao.insertAll(initialRecipes)
        }
    }
}