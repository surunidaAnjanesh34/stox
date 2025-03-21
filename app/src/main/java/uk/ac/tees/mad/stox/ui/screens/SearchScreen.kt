package uk.ac.tees.mad.stox.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.BestMatch
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData
import uk.ac.tees.mad.stox.model.dataclass.state.LoadingState
import uk.ac.tees.mad.stox.view.navigation.Dest
import uk.ac.tees.mad.stox.viewmodel.HomeScreenViewModel
import uk.ac.tees.mad.stox.viewmodel.SearchScreenViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchScreenViewModel = koinViewModel()
) {
    val offlineMode by viewModel.offlineMode.collectAsStateWithLifecycle()

    val searchScreenUiState by viewModel.searchScreenUiState.collectAsStateWithLifecycle()
    val dataFromDB by viewModel.dataFromDB.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val searchText by viewModel.searchInput.collectAsStateWithLifecycle()
    val isError by viewModel.isErrorInput.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    BackHandler {
        navController.navigate(Dest.HomeScreen){
            popUpTo(Dest.HomeScreen){
                inclusive = true
            }
        }
    }

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
                    Text(
                        text = "Search Stocks",
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
                    IconButton(
                        onClick = { navController.navigate(Dest.HomeScreen){
                            popUpTo(Dest.HomeScreen){
                                inclusive = true
                            }
                        } }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }, actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
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
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }, scrollBehavior = scrollBehavior
            )
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            DockedSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                inputField = {
                    SearchBarDefaults.InputField(
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (isError) SearchBarDefaults.inputFieldColors(MaterialTheme.colorScheme.error) else SearchBarDefaults.inputFieldColors(),
                        query = searchText,
                        onQueryChange = {
                            viewModel.updateSearchInput(it)
                            viewModel.updateIsErrorInput(false)
                        },
                        onSearch = { newQuery ->
//                            viewModel.updateSearchBarExpanded(false)
                            viewModel.onSearch()

                        },
                        expanded = false,
                        onExpandedChange = {
                            //viewModel.updateSearchBarExpanded(it)
                        },
                        placeholder = { Text("Search") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search, contentDescription = null
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(searchText.isNotBlank()) {
                                IconButton(onClick = {
                                    if (searchText.isNotBlank()) {
                                        viewModel.updateSearchInput("")
                                    }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                    )
                },
                expanded = false,
                onExpandedChange = {},
            ) {}
            when (searchScreenUiState) {
                is LoadingState.Loading -> {
                    // Show a loading indicator
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (searchText.isEmpty()) {
                            Text("Search for stocks")
                        }
                        if (isSearching) {
                            CircularProgressIndicator()
                        }

                    }
                }

                is LoadingState.Success -> {
                    val dataFromSearch =
                        (searchScreenUiState as LoadingState.Success<List<BestMatch>>).data
                    if (dataFromSearch.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("No stocks found")
                        }
                    } else {

                        SearchStocksList(
                            searchScreenStockDataList = dataFromSearch,
                            viewmodel = viewModel,
                            dataFromDB = dataFromDB,
                            navController = navController
                        )

                    }
                }

                is LoadingState.Error -> {
                    // Show an error message
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Error: ${(searchScreenUiState as LoadingState.Error).message}")
                    }
                }
            }
        }
    }
}


    @Composable
    fun SearchStocksList(
        searchScreenStockDataList: List<BestMatch>,
        viewmodel: SearchScreenViewModel,
        dataFromDB: List<HomeScreenStockData>,
        navController: NavHostController
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Search Results",
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
            items(searchScreenStockDataList, key = { it.symbol }) { stockItem ->
                SearchStockItem(searchScreenStockDataItem = stockItem, viewmodel = viewmodel, dataFromDB = dataFromDB, navController = navController)
            }
        }
    }

    @Composable
    fun SearchStockItem(
        searchScreenStockDataItem: BestMatch,
        viewmodel: SearchScreenViewModel,
        dataFromDB: List<HomeScreenStockData>,
        navController: NavHostController
    ) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .clickable{
                    navController.navigate(Dest.DetailsScreen(searchScreenStockDataItem.symbol))
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
                        text = searchScreenStockDataItem.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        if (searchScreenStockDataItem.symbol in dataFromDB.map { it.symbol }) {
                            viewmodel.remove(searchScreenStockDataItem.symbol)
                        } else {
                            viewmodel.insert(searchScreenStockDataItem.symbol)
                        }
                    }) {
                        if (searchScreenStockDataItem.symbol in dataFromDB.map { it.symbol }) {
                            Icon(
                                imageVector = Icons.Filled.BookmarkAdded,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkAdd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                            text = "${searchScreenStockDataItem.symbol}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Type: ${searchScreenStockDataItem.type}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )


                        Text(
                            text = "Region: ${searchScreenStockDataItem.region}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Currency: ${searchScreenStockDataItem.currency}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Bottom
                    ) {
//                    Text(
//                        text = "Previous Close: ₹${homeScreenStockDataItem.stockData.previousClose}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Text(
//                        text = "Vol: ${homeScreenStockDataItem.stockData.volume}",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
                        Text(
                            text = "Open: ${searchScreenStockDataItem.marketOpen} Close: ${searchScreenStockDataItem.marketClose}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Timezone: ${searchScreenStockDataItem.timezone}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }