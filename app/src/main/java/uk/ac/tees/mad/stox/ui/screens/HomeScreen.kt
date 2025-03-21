package uk.ac.tees.mad.stox.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.stox.R
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData
import uk.ac.tees.mad.stox.model.dataclass.state.LoadingState
import uk.ac.tees.mad.stox.view.navigation.Dest
import uk.ac.tees.mad.stox.viewmodel.HomeScreenViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewmodel: HomeScreenViewModel = koinViewModel(),
) {
    val offlineMode by viewmodel.offlineMode.collectAsStateWithLifecycle()

    val homeScreenUiState by viewmodel.homeScreenUiState.collectAsStateWithLifecycle()
    val dataFromDB by viewmodel.dataFromDB.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.stox),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(36.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(
                        text = "Stox",
                        maxLines = 1,
                        fontSize = 30.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }, navigationIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    AnimatedVisibility(offlineMode == true) {

                        Icon(
                            Icons.Outlined.CloudOff,
                            "Offline",
                            tint = MaterialTheme.colorScheme.error
                        )

                    }
                    AnimatedVisibility(offlineMode == false) {
                        Icon(Icons.Outlined.CloudDone, "Online", tint = Color.Green)
                    }
                }
            }, actions = {
                IconButton(onClick = {
                    navController.navigate(Dest.SearchScreen)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
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
            }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        when (homeScreenUiState) {
            is LoadingState.Loading -> {
                // Show a loading indicator
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is LoadingState.Success -> {
                val dataFromDB = dataFromDB
                    //(homeScreenUiState as LoadingState.Success<List<HomeScreenStockData>>).data
                if (dataFromDB.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No stocks added in favourites")
                        Text("Search for stocks to add to favourites")
                    }
                } else {
                    PullToRefreshBox(
                        isRefreshing = false,
                        onRefresh = { viewmodel.startLoading() },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        FavouriteStocksList(
                            innerPadding = innerPadding,
                            homeScreenStockDataList = dataFromDB,
                            viewmodel = viewmodel,
                            navController = navController
                        )
                    }

                }
            }

            is LoadingState.Error -> {
                // Show an error message
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Error: ${(homeScreenUiState as LoadingState.Error).message}")
                }
            }
        }

    }
}

@Composable
fun FavouriteStocksList(
    innerPadding: PaddingValues,
    homeScreenStockDataList: List<HomeScreenStockData>,
    viewmodel: HomeScreenViewModel,
    navController: NavHostController
) {
    LazyVerticalGrid(
        GridCells.Adaptive(400.dp), modifier = Modifier.fillMaxSize()
    ) {
        item(span = {
            GridItemSpan(maxLineSpan)
        }) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Favourite Stocks",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 2.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        items(homeScreenStockDataList, key = { it.id }) { stockItem ->
            FavouriteStockItem(homeScreenStockDataItem = stockItem, viewmodel = viewmodel, navController = navController)
        }
    }
}

@Composable
fun FavouriteStockItem(
    homeScreenStockDataItem: HomeScreenStockData,
    viewmodel: HomeScreenViewModel,
    navController: NavHostController
) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .clickable{
                navController.navigate(Dest.DetailsScreen(homeScreenStockDataItem.symbol))
            },

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
            }
            .padding(vertical = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homeScreenStockDataItem.symbol,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = { viewmodel.remove(homeScreenStockDataItem.symbol) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "₹${homeScreenStockDataItem.stockData.price}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (homeScreenStockDataItem.stockData.change.contains("-")) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(
                                text = "${homeScreenStockDataItem.stockData.change} (${homeScreenStockDataItem.stockData.changePercent})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color.Green
                            )
                            Text(
                                text = " +${homeScreenStockDataItem.stockData.change} (+${homeScreenStockDataItem.stockData.changePercent})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Green
                            )

                        }
                    }
                    Text(
                        text = "Last Updated:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${
                            SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(
                                    Date(
                                        homeScreenStockDataItem.timestamp!!
                                    )
                                )
                        }",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Previous Close: ₹${homeScreenStockDataItem.stockData.previousClose}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Vol: ${homeScreenStockDataItem.stockData.volume}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "H: ₹${homeScreenStockDataItem.stockData.high} L: ₹${homeScreenStockDataItem.stockData.low}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Last Trade: ${homeScreenStockDataItem.stockData.latestTradingDay}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}