package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.BestMatch
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockDataItem
import uk.ac.tees.mad.stox.model.dataclass.state.LoadingState
import uk.ac.tees.mad.stox.model.repository.AlphaVantageRepository
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.HomeScreenStockDataRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository
import uk.ac.tees.mad.stox.model.time.TrustedTimeManager

class SearchScreenViewModel(
    private val alphaVantageRepository: AlphaVantageRepository,
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
    private val homeScreenStockDataRepository: HomeScreenStockDataRepository,
    private val trustedTimeManager: TrustedTimeManager
) : ViewModel() {
    val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _userId = MutableStateFlow<String?>(authRepository.getCurrentUserId())
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _offlineMode = MutableStateFlow<Boolean?>(null)
    val offlineMode: StateFlow<Boolean?> = _offlineMode.asStateFlow()

    private val _isErrorInput = MutableStateFlow(false)
    val isErrorInput: StateFlow<Boolean> = _isErrorInput.asStateFlow()

    private val _searchInput = MutableStateFlow("")
    val searchInput: StateFlow<String> = _searchInput.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _dataFromDB = MutableStateFlow<List<HomeScreenStockData>>(emptyList())
    val dataFromDB: StateFlow<List<HomeScreenStockData>> = _dataFromDB.asStateFlow()

    private val _searchScreenUiState =
        MutableStateFlow<LoadingState<List<BestMatch>>>(LoadingState.Loading)
    val searchScreenUiState: StateFlow<LoadingState<List<BestMatch>>> =
        _searchScreenUiState.asStateFlow()

    init {
        observeNetworkConnectivity()
        loadDataFromDB()
    }

    private fun loadDataFromDB() {
        viewModelScope.launch {
            _dataFromDB.value = homeScreenStockDataRepository.getHomeScreenStockDataForUser(_userId.value.toString())
        }
    }

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkRepository.isNetworkAvailable.collect { isAvailable ->
                isNetworkAvailable.value = isAvailable
                if (isAvailable) {
                    _offlineMode.value = false
                    println("Internet is available")
                } else {
                    _offlineMode.value = true
                    println("Internet is not available")
                }
            }
        }
    }

    fun updateSearchInput(newInput: String){
        _searchInput.value = newInput
    }

    fun updateIsErrorInput(newInput: Boolean){
        _isErrorInput.value = newInput
    }

    fun onSearch(){
        viewModelScope.launch {
            _searchScreenUiState.value = LoadingState.Loading
            _isSearching.value = true
            val result = alphaVantageRepository.searchSymbol(_searchInput.value)
            result.onSuccess { response ->
                if (response.bestMatches != null) {
                    val bestMatches = response.bestMatches
                    _isSearching.value = false
                    _searchScreenUiState.value = LoadingState.Success(bestMatches)
                } else {
                    _isSearching.value = false
                    _isErrorInput.value = true
                    _searchScreenUiState.value =
                        LoadingState.Error("No data received from API\nNote: API rate limit is 25 requests per day")
                }
            }
            result.onFailure { error ->
                _isSearching.value = false
                _isErrorInput.value = true
                _searchScreenUiState.value =
                    LoadingState.Error(error.message ?: "Unknown error")
            }
        }
    }

    fun insert(symbol: String) {
        viewModelScope.launch {
            val result = alphaVantageRepository.getGlobalQuote(symbol)
            result.onSuccess { response ->
                if (response.globalQuote != null) {
                    val globalQuote = response.globalQuote
                    val currentTime = trustedTimeManager.getCurrentTimeInMillis()
                    val stockDataItemValues = HomeScreenStockDataItem(
                        open = globalQuote.open,
                        high = globalQuote.high,
                        low = globalQuote.low,
                        price = globalQuote.price,
                        volume = globalQuote.volume,
                        latestTradingDay = globalQuote.latestTradingDay,
                        previousClose = globalQuote.previousClose,
                        change = globalQuote.change,
                        changePercent = globalQuote.changePercent
                    )
                    val stockData = HomeScreenStockData(
                        userId = userId.value.toString(),
                        symbol = symbol,
                        stockData = stockDataItemValues,
                        timestamp = if (currentTime != null) currentTime else System.currentTimeMillis()
                    )
                    homeScreenStockDataRepository.insertHomeScreenStockData(stockData)
                }
            }
        }
    }

    fun remove(symbol: String) {
        viewModelScope.launch {
            homeScreenStockDataRepository.deleteHomeScreenStockDataForUserAndSymbol(
                _userId.value.toString(), symbol
            )
            loadDataFromDB()
        }
    }

//    fun isPresentinFavourites(symbol: String): Boolean {
//        return symbol in dataFromDB.value.map { it.symbol }
//    }
}