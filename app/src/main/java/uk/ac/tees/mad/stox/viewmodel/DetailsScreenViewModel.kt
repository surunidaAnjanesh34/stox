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

class DetailsScreenViewModel(
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

    private val _dataFromDB = MutableStateFlow<List<HomeScreenStockData>>(emptyList())
    val dataFromDB: StateFlow<List<HomeScreenStockData>> = _dataFromDB.asStateFlow()


    private val _globalQuoteState =
        MutableStateFlow<LoadingState<GlobalQuote>>(LoadingState.Loading)
    val globalQuoteState: StateFlow<LoadingState<GlobalQuote>> = _globalQuoteState.asStateFlow()

    init {
        observeNetworkConnectivity()
        loadDataFromDB()
    }

    private fun loadDataFromDB() {
        viewModelScope.launch {
            _dataFromDB.value =
                homeScreenStockDataRepository.getHomeScreenStockDataForUser(_userId.value.toString())
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

    fun getDetails(symbol: String) {
        viewModelScope.launch {
            _globalQuoteState.value = LoadingState.Loading
            val result = alphaVantageRepository.getGlobalQuote(symbol.toString())
            result.onSuccess { response ->
                if (response.globalQuote != null) {
                    val globalQuote = response.globalQuote
                    _globalQuoteState.value = LoadingState.Success(globalQuote)
                } else {
                    _globalQuoteState.value =
                        LoadingState.Error("No data received from API\nNote: API rate limit is 25 requests per day")
                }
            }
            result.onFailure { error ->
                _globalQuoteState.value = LoadingState.Error(error.message ?: "Unknown error")
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

}