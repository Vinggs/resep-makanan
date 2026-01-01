package com.example.resepku

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

fun shareRecipe(context: Context, recipe: Recipe) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "Coba resep ini: *${recipe.title}*\n\n${recipe.description}\n\nLihat di Aplikasi ResepKu!")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Resep via")
    context.startActivity(shareIntent)
}

fun extractTimeFromStep(step: String): Int? {
    val regex = Regex("(\\d+)\\s*(?:menit|mnt)")
    val match = regex.find(step.lowercase())
    return match?.groupValues?.get(1)?.toIntOrNull()
}

@Composable
fun StepTimer(durationMinutes: Int) {
    val totalSeconds = remember { durationMinutes * 60 }
    var timeLeft by remember { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (timeLeft == 0) {
            isRunning = false
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timeString = String.format("%02d:%02d", minutes, seconds)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = timeString, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(modifier = Modifier.width(16.dp))
        if (!isRunning && timeLeft == totalSeconds) {
            Button(onClick = { isRunning = true }, contentPadding = PaddingValues(horizontal = 12.dp), modifier = Modifier.height(36.dp)) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mulai")
            }
        } else {
            IconButton(onClick = { isRunning = !isRunning }) { Icon(if (isRunning) Icons.Default.Close else Icons.Default.PlayArrow, null) }
            if (timeLeft != totalSeconds) {
                IconButton(onClick = { isRunning = false; timeLeft = totalSeconds }) { Icon(Icons.Default.Refresh, null) }
            }
        }
    }
}

@Composable
fun RowScope.NutritionItem(label: String, value: String, accentColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(accentColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .weight(1f)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onFavoriteClick: () -> Unit,
    onRecipeClick: (String) -> Unit
) {
    val recipeList by viewModel.recipes.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories = viewModel.categories

    Scaffold(
        topBar = { TopAppBar(title = { Text("ResepKu", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)) },
        floatingActionButton = { FloatingActionButton(onClick = onFavoriteClick, containerColor = Color.Red, contentColor = Color.White) { Icon(Icons.Default.Favorite, "Fav") } }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { viewModel.onSearchTextChange(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp), placeholder = { Text("Cari resep...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { viewModel.onSearchTextChange("") }) { Icon(Icons.Default.Close, null) } },
                shape = RoundedCornerShape(100), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = Color.Gray)
            )

            Text("Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 8.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), selectedLabelColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp)) {
                if (recipeList.isEmpty()) item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { Text("Tidak ada resep.") } }
                else items(recipeList) { recipe -> RecipeCard(recipe, { onRecipeClick(recipe.id) }, { viewModel.toggleFavorite(recipe) }) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(viewModel: MainViewModel, onBack: () -> Unit, onRecipeClick: (String) -> Unit) {
    val favList by viewModel.favoriteRecipes.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Favorit") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }) }) { padding ->
        if (favList.isEmpty()) Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { Text("Belum ada favorit") }
        else LazyColumn(contentPadding = padding) { items(favList) { recipe -> RecipeCard(recipe, { onRecipeClick(recipe.id) }, { viewModel.toggleFavorite(recipe) }) } }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit, onFavoriteToggle: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable(onClick = onClick), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.size(120.dp).clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))) {
                Image(painter = painterResource(id = recipe.imageResId), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                IconButton(onClick = onFavoriteToggle, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).background(Color.Black.copy(0.3f), CircleShape).size(32.dp)) {
                    Icon(if (recipe.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (recipe.isFavorite) Color.Red else Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.weight(1f).padding(12.dp).align(Alignment.CenterVertically)) {
                Text(recipe.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(recipe.description, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(recipeId: String?, viewModel: MainViewModel, onBack: () -> Unit) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }
    val allRecipes by viewModel.recipes.collectAsState()
    val context = LocalContext.current
    val checkedState = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(recipeId, allRecipes) { if (recipeId != null) recipe = allRecipes.find { it.id == recipeId } }

    val ingredientsList = remember(recipe) { recipe?.ingredients?.split(",", "\n")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList() }
    val instructionsList = remember(recipe) { recipe?.instructions?.split(Regex("(?=\\d+\\.)|\\n"))?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList() }

    Scaffold { padding ->
        if (recipe != null) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(MaterialTheme.colorScheme.background)) {
                Box {
                    Image(painter = painterResource(id = recipe!!.imageResId), contentDescription = null, modifier = Modifier.fillMaxWidth().height(250.dp), contentScale = ContentScale.Crop)
                    IconButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp).background(Color.Black.copy(0.4f), CircleShape)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
                    Row(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                        IconButton(onClick = { shareRecipe(context, recipe!!) }, modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)) { Icon(Icons.Default.Share, null, tint = Color.White) }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.toggleFavorite(recipe!!) }, modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape)) {
                            Icon(if (recipe!!.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (recipe!!.isFavorite) Color.Red else Color.White)
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().offset(y = (-20).dp),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(recipe!!.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NutritionItem("Kalori", recipe!!.calories, Color(0xFFE65100))
                            NutritionItem("Karbo", recipe!!.carbs, Color(0xFF1976D2))
                            NutritionItem("Protein", recipe!!.protein, Color(0xFF2E7D32))
                            NutritionItem("Lemak", recipe!!.fat, Color(0xFFC2185B))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(recipe!!.description)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Row { Text("Bahan-bahan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Text("(Checklist)", style = MaterialTheme.typography.bodySmall, color = Color.Gray) }
                        Spacer(modifier = Modifier.height(8.dp))
                        ingredientsList.forEach { ing ->
                            val isChecked = checkedState[ing] ?: false
                            Row(Modifier.fillMaxWidth().clickable { checkedState[ing] = !isChecked }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = isChecked, onCheckedChange = { checkedState[ing] = it })
                                Text(ing, textDecoration = if (isChecked) TextDecoration.LineThrough else null, color = if (isChecked) Color.Gray else Color.Black)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Text("Cara Membuat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        instructionsList.forEachIndexed { i, step ->
                            val time = extractTimeFromStep(step)
                            Column(Modifier.padding(vertical = 8.dp)) {
                                Text(step, style = MaterialTheme.typography.bodyLarge)
                                if (time != null) StepTimer(time)
                            }
                            if (i < instructionsList.size - 1) HorizontalDivider(color = Color.LightGray.copy(0.5f))
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}