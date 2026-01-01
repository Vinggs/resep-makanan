package com.example.resepku

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// PENTING: Pastikan import R ini ada agar bisa akses gambar di folder drawable
import com.example.resepku.R

class MainViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    init {
        // Data Dummy 7 Resep dengan Gambar
        // Pastikan nama file gambar di R.drawable.nama_file sesuai dengan yang Anda copy
        _recipes.value = listOf(
            Recipe(
                title = "Nasi Goreng",
                description = "Nasi goreng spesial pedas dengan telur mata sapi.",
                ingredients = "Nasi putih, 2 siung Bawang merah, 1 siung Bawang putih, Cabai, Kecap Manis, Telur, Garam",
                instructions = "1. Haluskan bawang merah, bawang putih, dan cabai.\n2. Panaskan minyak, tumis bumbu hingga harum.\n3. Masukkan telur, buat orak-arik.\n4. Masukkan nasi, tambahkan kecap dan garam.\n5. Aduk rata hingga matang dan sajikan.",
                imageResId = R.drawable.nasi_goreng
            ),
            Recipe(
                title = "Sate Ayam",
                description = "Sate ayam dengan bumbu kacang yang kental dan gurih.",
                ingredients = "500gr Daging Ayam, Kacang tanah goreng, Kecap manis, Bawang merah, Jeruk limau",
                instructions = "1. Potong dadu daging ayam, tusuk dengan tusuk sate.\n2. Haluskan kacang tanah, campur dengan air dan kecap untuk bumbu.\n3. Bakar sate sambil diolesi bumbu hingga matang.\n4. Sajikan dengan sisa bumbu kacang dan irisan bawang.",
                imageResId = R.drawable.sate_ayam
            ),
            Recipe(
                title = "Rendang",
                description = "Daging sapi masak santan kaya rempah khas Padang, dimasak lama.",
                ingredients = "1kg Daging Sapi, 1 liter Santan kental, Serai, Daun jeruk, Lengkuas, Bumbu Rendang Instan (opsional)",
                instructions = "1. Potong daging sapi sesuai selera.\n2. Rebus santan dengan bumbu halus/instan, serai, dan daun jeruk.\n3. Masukkan daging, masak dengan api kecil sambil diaduk.\n4. Masak hingga santan mengering dan mengeluarkan minyak (sekitar 3-4 jam).",
                imageResId = R.drawable.rendang
            ),
            Recipe(
                title = "Soto Betawi",
                description = "Soto kuah susu yang creamy dengan isian daging dan jeroan.",
                ingredients = "Daging sapi/jeroan, Susu cair/santan, Kentang goreng, Tomat, Emping, Daun bawang",
                instructions = "1. Rebus daging hingga empuk.\n2. Tumis bumbu soto, masukkan ke air rebusan daging.\n3. Tuang susu cair, aduk agar tidak pecah.\n4. Sajikan dengan potongan kentang, tomat, dan emping.",
                imageResId = R.drawable.soto
            ),
            Recipe(
                title = "Gado-gado",
                description = "Salad sayuran segar disiram saus kacang.",
                ingredients = "Bayam, Tauge, Kacang panjang, Tahu, Tempe, Bumbu pecel/kacang, Kerupuk",
                instructions = "1. Rebus semua sayuran hingga matang, tiriskan.\n2. Goreng tahu dan tempe, potong-potong.\n3. Larutkan bumbu kacang dengan air hangat.\n4. Tata sayuran, siram bumbu kacang, taburi kerupuk.",
                imageResId = R.drawable.gado_gado
            ),
            Recipe(
                title = "Bakso Sapi",
                description = "Bakso sapi kenyal dengan kuah kaldu yang segar dan hangat.",
                ingredients = "Bakso sapi, Mie kuning/bihun, Sawi hijau, Bawang goreng, Seledri, Kaldu sapi",
                instructions = "1. Rebus air dengan tulang sapi untuk kaldu.\n2. Masukkan bumbu kuah bakso (bawang putih geprek, lada, garam).\n3. Masukkan bakso hingga mengapung.\n4. Sajikan di mangkuk bersama mie dan sawi, siram kuah panas.",
                imageResId = R.drawable.bakso
            ),
            Recipe(
                title = "Mie Ayam",
                description = "Mie kenyal dengan topping ayam kecap manis gurih.",
                ingredients = "Mie telur, Daging ayam cincang, Kecap manis, Saus tiram, Sawi hijau, Minyak bawang",
                instructions = "1. Tumis ayam dengan bawang putih, kecap manis, dan saus tiram hingga matang.\n2. Rebus mie dan sawi.\n3. Siapkan mangkuk, beri minyak bawang dan kecap asin.\n4. Masukkan mie, aduk rata. Beri topping ayam di atasnya.",
                imageResId = R.drawable.mie_ayam
            )
        )
    }

    fun getRecipeById(id: String): Recipe? {
        return _recipes.value.find { it.id == id }
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

    // Update: Saat menambah resep baru, kita pakai gambar placeholder dulu
    // (Karena fitur upload gambar dari galeri cukup rumit untuk pemula)
    fun addRecipe(title: String, desc: String, ingredients: String, instructions: String) {
        val newRecipe = Recipe(
            title = title,
            description = desc,
            ingredients = ingredients,
            instructions = instructions,
            imageResId = R.drawable.placeholder_food // Gambar default untuk resep baru
        )
        _recipes.value = _recipes.value + newRecipe
    }
}