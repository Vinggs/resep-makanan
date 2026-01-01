package com.example.resepku

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

// --- HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onFavoriteClick: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    // Ambil data dari ViewModel
    val recipeList by viewModel.recipes.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = viewModel.categories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ResepKu", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFavoriteClick,
                containerColor = Color.Red,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Lihat Favorit")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // --- BAGIAN KATEGORI (BARU) ---
            Text(
                text = "Kategori",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), // Warna hijau muda saat aktif
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = category == selectedCategory,
                            borderColor = if (category == selectedCategory) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    )
                }
            }

            // --- DAFTAR RESEP ---
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp, top = 8.dp)
            ) {
                if (recipeList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Tidak ada resep di kategori ini.")
                        }
                    }
                } else {
                    items(recipeList) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(recipe) }
                        )
                    }
                }
            }
        }
    }
}

// --- FAVORITE SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    val favList by viewModel.favoriteRecipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Menu Favorit", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (favList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Belum ada resep favorit.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(favList) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        onFavoriteToggle = { viewModel.toggleFavorite(recipe) }
                    )
                }
            }
        }
    }
}

// --- KOMPONEN KARTU RESEP (UPDATE: Posisi Love di dalam Gambar) ---
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // KITA GUNAKAN BOX UNTUK MENUMPUK GAMBAR DAN IKON LOVE
            Box(
                modifier = Modifier
                    .size(120.dp) // Ukuran area gambar diperbesar sedikit agar proporsional
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            ) {
                // 1. Gambar Resep (Lapisan Bawah)
                Image(
                    painter = painterResource(id = recipe.imageResId),
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(), // Gambar memenuhi Box
                    contentScale = ContentScale.Crop
                )

                // 2. Tombol Love (Lapisan Atas/Overlay)
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Posisikan di Pojok Kanan Atas
                        .padding(4.dp) // Jarak dari pinggir
                        // Beri background transparan agar terlihat di gambar terang/gelap
                        .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
                        .size(32.dp) // Ukuran tombol diperkecil sedikit agar rapi
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (recipe.isFavorite) Color.Red else Color.White, // Putih jika belum dilove agar kontras
                        modifier = Modifier.size(20.dp) // Ukuran ikon di dalam tombol
                    )
                }
            }

            // Bagian Teks di sebelah kanan (Tidak berubah)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
                    .align(Alignment.CenterVertically) // Teks rata tengah secara vertikal
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3, // Bisa menampilkan deskripsi sedikit lebih banyak
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// --- DETAIL SCREEN (UPDATE: Posisi Love di Pojok Kanan Atas Banner) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: String?,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    val allRecipes by viewModel.recipes.collectAsState()

    LaunchedEffect(recipeId, allRecipes) {
        if (recipeId != null) {
            recipe = allRecipes.find { it.id == recipeId }
        }
    }

    Scaffold { padding ->
        if (recipe != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box {
                    // Gambar Banner
                    Image(
                        painter = painterResource(id = recipe!!.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Tombol Back (Pojok Kiri Atas)
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }

                    // UPDATE: Tombol Love (Dipindah ke Pojok Kanan Atas, gaya disamakan dengan tombol back)
                    IconButton(
                        onClick = { viewModel.toggleFavorite(recipe!!) },
                        modifier = Modifier
                            .align(Alignment.TopEnd) // Pojok Kanan Atas
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = if (recipe!!.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            // Merah jika favorit, Putih jika tidak (agar kontras dengan background gelap)
                            tint = if (recipe!!.isFavorite) Color.Red else Color.White
                        )
                    }
                }

                // Kartu Konten (Tidak lagi berpotongan dengan tombol love)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp), // Tetap naik sedikit menutupi gambar bawah
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(recipe!!.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("Deskripsi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(recipe!!.description)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Bahan-bahan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(recipe!!.ingredients)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Cara Membuat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(recipe!!.instructions)
                    }
                }
            }
        }
    }
}