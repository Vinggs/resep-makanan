package com.example.resepku

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// --- 1. Login Screen (Sedikit styling tambahan) ---
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Menggunakan Box dengan background warna primary
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Masuk ResepKu", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { if (viewModel.login(username, password)) onLoginSuccess() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Masuk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 2. Home Screen (Update: Menambahkan Gambar pada List) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onAddClick: () -> Unit,
    onLogout: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    val recipeList by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Resep", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Resep")
            }
        },
        containerColor = MaterialTheme.colorScheme.background // Warna background krem
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(recipeList) { recipe ->
                RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
            }
        }
    }
}

// Komponen Card Terpisah agar lebih rapi
@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Gambar Thumbnail di sebelah kiri
            Image(
                painter = painterResource(id = recipe.imageResId),
                contentDescription = recipe.title,
                modifier = Modifier
                    .size(120.dp) // Ukuran gambar kotak
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)), // Clip sudut kiri
                contentScale = ContentScale.Crop // Potong gambar agar pas di kotak
            )

            // Teks di sebelah kanan
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3, // Batasi teks deskripsi maksimal 3 baris
                    overflow = TextOverflow.Ellipsis // Tambah titik-titik jika kepanjangan
                )
            }
        }
    }
}


// --- 3. Add Recipe Screen (Update styling) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    viewModel: MainViewModel,
    onRecipeAdded: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Resep Baru") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Card( // Bungkus form dengan Card agar lebih rapi
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Info bahwa gambar akan pakai placeholder
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_food),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Resep baru akan menggunakan gambar default.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul Masakan") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi Singkat") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Bahan-bahan") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Cara Membuat") }, modifier = Modifier.fillMaxWidth(), minLines = 5)

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        if (title.isNotEmpty()) {
                            viewModel.addRecipe(title, desc, ingredients, instructions)
                            onRecipeAdded()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Simpan Resep", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- 4. Detail Screen (Update: Menambahkan Gambar Banner Besar) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    recipeId: String?,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val recipe = recipeId?.let { viewModel.getRecipeById(it) }

    Scaffold(
        // Kita buat top bar transparan agar gambar bisa full ke atas (opsional, tapi terlihat lebih modern)
    ) { padding ->
        if (recipe != null) {
            Column(
                modifier = Modifier
                    //.padding(padding) // Hapus padding ini agar gambar mentok atas
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box {
                    // Gambar Banner Besar di Atas
                    Image(
                        painter = painterResource(id = recipe.imageResId),
                        contentDescription = recipe.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp), // Tinggi banner
                        contentScale = ContentScale.Crop
                    )
                    // Tombol Back di atas gambar
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                }

                // Konten Teks di bawah gambar (dalam Card melengkung ke atas)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-20).dp), // Tarik ke atas sedikit menutupi gambar
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(recipe.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))

                        SectionTitle(title = "Deskripsi")
                        Text(recipe.description, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SectionTitle(title = "Bahan-bahan")
                        Text(recipe.ingredients, style = MaterialTheme.typography.bodyMedium)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        SectionTitle(title = "Cara Membuat")
                        Text(recipe.instructions, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Resep tidak ditemukan")
            }
        }
    }
}

// Komponen kecil untuk judul bagian di detail
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}