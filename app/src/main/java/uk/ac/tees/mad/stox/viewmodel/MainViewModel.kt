package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stox.model.dataclass.alphavantage.GlobalQuote
import uk.ac.tees.mad.stox.model.dataclass.state.LoadingState
import uk.ac.tees.mad.stox.model.repository.AlphaVantageRepository
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository

class MainViewModel(
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
    private val alphaVantageRepository: AlphaVantageRepository
) : ViewModel() {

    private val _databaseIsEmpty = MutableStateFlow(true)
    val databaseIsEmpty: StateFlow<Boolean> = _databaseIsEmpty.asStateFlow()

    private val _dataFromDB = MutableStateFlow<List<Any>>(emptyList())
    val dataFromDB: StateFlow<List<Any>> = _dataFromDB.asStateFlow()

    private val _globalQuoteState = MutableStateFlow<LoadingState<GlobalQuote>>(LoadingState.Loading)
    val globalQuoteState: StateFlow<LoadingState<GlobalQuote>> = _globalQuoteState.asStateFlow()

    val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _offlineMode = MutableStateFlow(false)
    val offlineMode: StateFlow<Boolean> = _offlineMode.asStateFlow()

    // Try to load data from database
    // example: val data = databaseRepository.loadFromDatabase()
    // if(data.isNotEmpty()){
    // _databaseIsEmpty.value = false
    // _dataFromDB.value = data
    // }

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
            // Try to load data from database
            // example: val data = databaseRepository.loadFromDatabase()
            // if(data.isNotEmpty()){
            // _databaseIsEmpty.value = false
            // _dataFromDB.value = data
            // }
        }
    }

    fun getGlobalQuote(symbol: String) {
        viewModelScope.launch {
            _globalQuoteState.value = LoadingState.Loading
            val result = alphaVantageRepository.getGlobalQuote(symbol)
            result.onSuccess { response ->
                _globalQuoteState.value = LoadingState.Success(response.globalQuote)
            }
            result.onFailure { error ->
                _globalQuoteState.value = LoadingState.Error(error.message ?: "Unknown error")
            }
        }
    }

}