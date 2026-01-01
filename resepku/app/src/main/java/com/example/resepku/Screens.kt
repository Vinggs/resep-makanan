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
import com.example.resepku.R

// --- LOGIN SCREEN ---
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
        Card(
            modifier = Modifier.align(Alignment.Center).padding(32.dp).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Masuk ResepKu", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { if (viewModel.login(username, password)) onLoginSuccess() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Masuk", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- HOME SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, onAddClick: () -> Unit, onLogout: () -> Unit, onRecipeClick: (String) -> Unit) {
    val recipeList by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Resep", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, actionIconContentColor = Color.White),
                actions = { IconButton(onClick = { viewModel.logout(); onLogout() }) { Icon(Icons.Default.ExitToApp, "Logout") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = MaterialTheme.colorScheme.tertiary, contentColor = Color.White) {
                Icon(Icons.Default.Add, "Tambah Resep")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(recipeList) { recipe ->
                RecipeCard(recipe = recipe, onClick = { onRecipeClick(recipe.id) })
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = recipe.imageResId), contentDescription = recipe.title,
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(recipe.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(recipe.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// --- ADD RECIPE SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(viewModel: MainViewModel, onRecipeAdded: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tambah Resep Baru") }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)) }
    ) { padding ->
        Card(modifier = Modifier.padding(padding).padding(16.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                    Image(painter = painterResource(id = R.drawable.placeholder_food), contentDescription = null, modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Gambar default akan digunakan.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = ingredients, onValueChange = { ingredients = it }, label = { Text("Bahan-bahan") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = instructions, onValueChange = { instructions = it }, label = { Text("Cara Membuat") }, modifier = Modifier.fillMaxWidth(), minLines = 5)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { if (title.isNotEmpty()) { viewModel.addRecipe(title, desc, ingredients, instructions); onRecipeAdded() } }, modifier = Modifier.fillMaxWidth()) {
                    Text("Simpan Resep")
                }
            }
        }
    }
}

// --- DETAIL SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(recipeId: String?, viewModel: MainViewModel, onBack: () -> Unit) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }

    // Ambil data dari database (async)
    LaunchedEffect(recipeId) {
        if (recipeId != null) {
            recipe = viewModel.getRecipeById(recipeId)
        }
    }

    Scaffold { padding ->
        if (recipe != null) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.background)) {
                Box {
                    Image(painter = painterResource(id = recipe!!.imageResId), contentDescription = null, modifier = Modifier.fillMaxWidth().height(250.dp), contentScale = ContentScale.Crop)
                    IconButton(onClick = onBack, modifier = Modifier.padding(16.dp).background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(50))) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
                    }
                }
                Card(modifier = Modifier.fillMaxWidth().offset(y = (-20).dp), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(recipe!!.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Deskripsi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text(recipe!!.description)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Text("Bahan-bahan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text(recipe!!.ingredients)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Text("Cara Membuat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text(recipe!!.instructions)
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Memuat Resep...") }
        }
    }
}