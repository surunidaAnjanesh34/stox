package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.GlobalQuote
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockData
import uk.ac.tees.mad.stox.model.dataclass.room.HomeScreenStockDataItem
import uk.ac.tees.mad.stox.model.dataclass.state.LoadingState
import uk.ac.tees.mad.stox.model.repository.AlphaVantageRepository
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.HomeScreenStockDataRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository
import uk.ac.tees.mad.stox.model.time.TrustedTimeManager

class HomeScreenViewModel(
    private val alphaVantageRepository: AlphaVantageRepository,
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
    private val homeScreenStockDataRepository: HomeScreenStockDataRepository,
    private val trustedTimeManager: TrustedTimeManager
) : ViewModel() {
    val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _offlineMode = MutableStateFlow<Boolean?>(null)
    val offlineMode: StateFlow<Boolean?> = _offlineMode.asStateFlow()

    private val _userId = MutableStateFlow<String?>(authRepository.getCurrentUserId())
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _globalQuoteState =
        MutableStateFlow<LoadingState<GlobalQuote>>(LoadingState.Loading)
    val globalQuoteState: StateFlow<LoadingState<GlobalQuote>> = _globalQuoteState.asStateFlow()

    private val _homeScreenUiState =
        MutableStateFlow<LoadingState<List<HomeScreenStockData>>>(LoadingState.Loading)
    val homeScreenUiState: StateFlow<LoadingState<List<HomeScreenStockData>>> =
        _homeScreenUiState.asStateFlow()

    init {
        observeNetworkConnectivity()
        startLoading()
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

    fun startLoading() {
        viewModelScope.launch {
            _homeScreenUiState.value = LoadingState.Loading
            _userId.value = getCurrentUserId().toString()
            if (homeScreenStockDataRepository.getHomeScreenStockDataCountForUser(_userId.value.toString()) == 0) {
                _homeScreenUiState.value = LoadingState.Success(emptyList())
            } else {
                if (offlineMode.value == true) {
                    _homeScreenUiState.value = LoadingState.Success(
                        homeScreenStockDataRepository.getHomeScreenStockDataForUser(_userId.value.toString())
                    )
                } else {
                    _homeScreenUiState.value = LoadingState.Loading
                    val dataFromDB =
                        homeScreenStockDataRepository.getHomeScreenStockDataForUser(_userId.value.toString())
                    for (data in dataFromDB) {
                        val result = alphaVantageRepository.getGlobalQuote(data.symbol)
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
                                    symbol = data.symbol,
                                    stockData = stockDataItemValues,
                                    timestamp = if (currentTime != null) currentTime else System.currentTimeMillis()
                                )
                                homeScreenStockDataRepository.deleteHomeScreenStockDataForUserAndSymbol(
                                    userId.value.toString(), data.symbol
                                )
                                homeScreenStockDataRepository.insertHomeScreenStockData(stockData)
                            } else {
                                _homeScreenUiState.value =
                                    LoadingState.Error("No data received from API\nNote: API rate limit is 25 requests per day")
                                return@launch
                            }
                        }
                        result.onFailure { error ->
                            _homeScreenUiState.value =
                                LoadingState.Error(error.message ?: "Unknown error")
                            return@launch
                        }
                    }
                    _homeScreenUiState.value = LoadingState.Success(
                        homeScreenStockDataRepository.getHomeScreenStockDataForUser(_userId.value.toString())
                    )
                }
            }
        }
    }

    fun insert() {
        viewModelScope.launch {
            val result = alphaVantageRepository.getGlobalQuote("300135.SHZ")
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
                        symbol = "300135.SHZ",
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
            _homeScreenUiState.value = LoadingState.Success(
                homeScreenStockDataRepository.getHomeScreenStockDataForUser(_userId.value.toString())
            )
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }
}