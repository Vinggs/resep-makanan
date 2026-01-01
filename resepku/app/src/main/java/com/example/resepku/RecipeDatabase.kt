package com.example.resepku

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.resepku.R

@Database(entities = [Recipe::class], version = 4, exportSchema = false) // Version 4
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
                    .fallbackToDestructiveMigration()
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

            val initialRecipes = listOf(
                Recipe(
                    title = "Nasi Goreng Spesial",
                    description = "Nasi goreng pedas gurih dengan telur mata sapi.",
                    ingredients = "1 piring Nasi putih, 2 siung Bawang merah, 1 siung Bawang putih, 5 buah Cabai rawit, Kecap Manis, 1 butir Telur, Garam",
                    instructions = "1. Haluskan bawang merah, bawang putih, dan cabai.\n2. Panaskan minyak, lalu tumis bumbu halus selama 3 menit hingga harum matang.\n3. Masukkan telur, buat orak-arik sebentar.\n4. Masukkan nasi, tambahkan kecap dan garam, aduk rata hingga matang.",
                    imageResId = R.drawable.nasi_goreng,
                    category = "Sarapan",
                    calories = "450 Kcal", carbs = "60g", protein = "12g", fat = "15g"
                ),
                Recipe(
                    title = "Sate Ayam Madura",
                    description = "Sate ayam empuk dengan bumbu kacang kental.",
                    ingredients = "500gr Daging Ayam, 100gr Kacang tanah goreng, Kecap manis, Bawang merah, Jeruk limau",
                    instructions = "1. Potong dadu daging ayam, tusuk dengan tusuk sate.\n2. Haluskan kacang tanah, campur dengan air dan kecap untuk bumbu.\n3. Bakar sate di atas arang selama 15 menit sambil dibolak-balik dan diolesi bumbu.\n4. Sajikan dengan sisa bumbu kacang dan irisan bawang.",
                    imageResId = R.drawable.sate_ayam,
                    category = "Makan Malam",
                    calories = "320 Kcal", carbs = "15g", protein = "28g", fat = "18g"
                ),
                Recipe(
                    title = "Rendang Sapi",
                    description = "Daging sapi masak santan kaya rempah.",
                    ingredients = "1kg Daging Sapi, 1 liter Santan kental, Serai, Daun jeruk, Lengkuas, Bumbu Rendang",
                    instructions = "1. Potong daging sapi sesuai selera.\n2. Rebus santan dengan bumbu halus, serai, dan daun jeruk sampai mendidih.\n3. Masukkan daging, masak dengan api kecil selama 120 menit agar daging empuk dan bumbu meresap.\n4. Teruskan memasak hingga kuah kering berminyak.",
                    imageResId = R.drawable.rendang,
                    category = "Tradisional",
                    calories = "550 Kcal", carbs = "10g", protein = "35g", fat = "40g"
                ),
                Recipe(
                    title = "Soto Betawi",
                    description = "Soto kuah susu creamy dengan isian daging.",
                    ingredients = "500gr Daging sapi, 500ml Susu cair, Kentang goreng, Tomat, Emping, Daun bawang",
                    instructions = "1. Rebus daging sapi dalam air mendidih selama 45 menit hingga empuk.\n2. Tumis bumbu soto, masukkan ke air rebusan daging.\n3. Tuang susu cair, aduk perlahan agar santan/susu tidak pecah.\n4. Sajikan panas dengan potongan kentang, tomat, dan emping.",
                    imageResId = R.drawable.soto,
                    category = "Makan Siang",
                    calories = "380 Kcal", carbs = "25g", protein = "22g", fat = "24g"
                ),
                Recipe(
                    title = "Gado-gado",
                    description = "Salad sayuran segar Indonesia dengan saus kacang.",
                    ingredients = "Ikat Bayam, Tauge, Kacang panjang, Tahu, Tempe, Bumbu pecel instan, Kerupuk",
                    instructions = "1. Rebus sayuran (bayam, tauge, kacang panjang) selama 5 menit, lalu tiriskan.\n2. Goreng tahu dan tempe hingga kecokelatan, potong dadu.\n3. Larutkan bumbu kacang dengan air panas.\n4. Tata sayuran di piring, siram bumbu kacang, taburi kerupuk.",
                    imageResId = R.drawable.gado_gado,
                    category = "Diet",
                    calories = "280 Kcal", carbs = "35g", protein = "14g", fat = "10g"
                ),
                Recipe(
                    title = "Mie Rebus Telur",
                    description = "Mie instan rebus sederhana pakai telur.",
                    ingredients = "1 bungkus Mie Instan, 1 butir Telur, Sawi hijau, Cabai potong",
                    instructions = "1. Didihkan air di panci.\n2. Masukkan mie dan telur, rebus selama 3 menit.\n3. Masukkan bumbu di mangkuk.\n4. Tuang mie dan kuah ke mangkuk, aduk rata.",
                    imageResId = R.drawable.mie_ayam,
                    category = "Jajanan",
                    calories = "410 Kcal", carbs = "55g", protein = "9g", fat = "18g"
                )
            )

            dao.insertAll(initialRecipes)
        }
    }
}