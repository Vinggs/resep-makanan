package com.example.resepku

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.resepku.R // Pastikan import R ada

@Database(entities = [Recipe::class], version = 1, exportSchema = false)
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
            // DATA AWAL (SEED)
            val initialRecipes = listOf(
                Recipe(
                    title = "Nasi Goreng",
                    description = "Nasi goreng spesial pedas dengan telur mata sapi.",
                    ingredients = "Nasi putih, Bawang, Cabai, Kecap, Telur",
                    instructions = "1. Tumis bumbu.\n2. Masukkan nasi dan kecap.\n3. Sajikan dengan telur.",
                    imageResId = R.drawable.nasi_goreng
                ),
                Recipe(
                    title = "Sate Ayam",
                    description = "Sate ayam dengan bumbu kacang.",
                    ingredients = "Daging Ayam, Kacang, Kecap",
                    instructions = "1. Tusuk ayam.\n2. Bakar.\n3. Sajikan bumbu kacang.",
                    imageResId = R.drawable.sate_ayam
                ),
                Recipe(
                    title = "Rendang",
                    description = "Daging sapi masak santan khas Padang.",
                    ingredients = "Daging Sapi, Santan, Rempah",
                    instructions = "1. Masak daging dan santan hingga kering.",
                    imageResId = R.drawable.rendang
                ),
                Recipe(
                    title = "Soto Betawi",
                    description = "Soto kuah susu creamy.",
                    ingredients = "Daging, Susu, Kentang",
                    instructions = "1. Rebus daging.\n2. Tuang susu dan bumbu.",
                    imageResId = R.drawable.soto
                ),
                Recipe(
                    title = "Gado-gado",
                    description = "Salad sayuran saus kacang.",
                    ingredients = "Sayuran, Bumbu Kacang",
                    instructions = "1. Rebus sayur.\n2. Siram bumbu.",
                    imageResId = R.drawable.gado_gado
                ),
                Recipe(
                    title = "Bakso Sapi",
                    description = "Bakso kuah segar.",
                    ingredients = "Bakso, Mie, Kuah",
                    instructions = "1. Rebus kuah.\n2. Masukkan bakso.",
                    imageResId = R.drawable.bakso
                ),
                Recipe(
                    title = "Mie Ayam",
                    description = "Mie topping ayam kecap.",
                    ingredients = "Mie, Ayam kecap",
                    instructions = "1. Rebus mie.\n2. Beri topping.",
                    imageResId = R.drawable.mie_ayam
                )
            )
            dao.insertAll(initialRecipes)
        }
    }
}