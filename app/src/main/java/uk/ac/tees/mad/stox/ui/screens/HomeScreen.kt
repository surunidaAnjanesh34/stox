package uk.ac.tees.mad.stox.ui.screens

import android.content.Context
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.tees.mad.stox.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Stox",
                    maxLines = 1,
                    fontSize = 30.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    Row{
                    Spacer(modifier = Modifier.padding(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.stox),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(36.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )}
                },
                actions = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Localized description",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        FavouriteStocksList(innerPadding=innerPadding)
    }
}

@Composable
fun FavouriteStocksList(innerPadding: PaddingValues){
    LazyVerticalGrid(GridCells.Adaptive(400.dp), modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        item(
            span = {
                GridItemSpan(maxLineSpan)
            }
        ){
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ){
                Text(text = "Favourite Stocks",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 2.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        item{
            FavouriteStockItem()
        }
        item{
            FavouriteStockItem()
        }
        item{
            FavouriteStockItem()
        }
        item{
            FavouriteStockItem()
        }
        item{
            FavouriteStockItem()
        }
    }
}

@Composable
fun FavouriteStockItem(){
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),

        border = BorderStroke(
            2.dp, brush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.surfaceContainerLowest,
                    MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                startX = 0f,
                endX = 1000f,
                tileMode = TileMode.Mirror,
            )
        )
    ) {
        // Infinite transition for the background gradient animation
        val infiniteTransition = rememberInfiniteTransition(label = "background")
        val targetOffset = with(LocalDensity.current) {
            1000.dp.toPx()
        }
        val offset by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = targetOffset, animationSpec = infiniteRepeatable(
                tween(20000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
            ), label = "offset"
        )
        val brushColors = listOf(
            MaterialTheme.colorScheme.surfaceContainerHighest,
            MaterialTheme.colorScheme.surfaceContainerLowest
        )

        Column(modifier = Modifier
            .drawWithCache {
                val brushSize = 400f
                val brush = Brush.linearGradient(
                    colors = brushColors,
                    start = Offset(offset, offset),
                    end = Offset(offset + brushSize, offset + brushSize),
                    tileMode = TileMode.Mirror
                )
                onDrawBehind {
                    drawRect(brush)
                }
            }.padding(vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                Text(text = "WIPRO.BSE",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface)
                IconButton(
                    onClick = {/* doSomething() */}
                ) {
                    Icon(imageVector = Icons.Filled.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column (
                ) {
                    Text(text = "₹313.15",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold)
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = Color.Red)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(text = "-0.90 (-0.2866%)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red)
                    }
                }
                Column (
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text="Previous Close: ₹314.05",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(text="Vol: 257273",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "H: ₹314.30 L: ₹310.40",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text(text = "Last Trade: 2025-02-20",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    HomeScreen(
        navController = NavHostController(
            LocalContext.current
        )
    )
}