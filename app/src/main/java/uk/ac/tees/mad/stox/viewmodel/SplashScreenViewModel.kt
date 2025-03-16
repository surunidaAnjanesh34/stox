package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stox.model.dataclass.state.LoadingState
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.HomeScreenStockDataRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository

class SplashScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
    private val homeScreenStockDataRepository: HomeScreenStockDataRepository,
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState<Any>>(LoadingState.Loading)
    val loadingState: StateFlow<LoadingState<Any>> = _loadingState.asStateFlow()

    val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private fun observeNetworkConnectivity() {
        viewModelScope.launch {
            networkRepository.isNetworkAvailable.collect { isAvailable ->
                isNetworkAvailable.value = isAvailable
                if (isAvailable) {
                    println("Internet is available")
                } else {
                    println("Internet is not available")
                }
            }
        }
    }

    init {
        observeNetworkConnectivity()
        startLoading()
    }

    fun startLoading() {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            networkRepository.isNetworkAvailable.collectLatest { isAvailable ->
                if (isAvailable) {
                    _loadingState.value = LoadingState.Loading
                    delay(4000)
                    _loadingState.value = LoadingState.Success(Any())
                } else {
                    _loadingState.value = LoadingState.Loading
                    delay(4000)
                    val message = "No internet connection"
                    if (authRepository.isSignedIn()) {
                        if (homeScreenStockDataRepository.getHomeScreenStockDataCountForUser(
                                getCurrentUserId().toString()
                            ) == 0
                        ) {
                            _loadingState.value = LoadingState.Error(message)
                        } else {

                            _loadingState.value = LoadingState.Success(Any())

                        }
                    } else {
                        _loadingState.value = LoadingState.Error(message)
                    }
                }
            }
        }
    }

    fun getCurrentUserId(): String? {
        return authRepository.getCurrentUserId()
    }

    fun isSignedIn(): Boolean {
        return authRepository.isSignedIn()
    }
}