package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stox.model.dataclass.splashscreen.LoadingState
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository

class SplashScreenViewModel(
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _loadingState = MutableStateFlow<LoadingState<Any>>(LoadingState.Loading)
    val loadingState: StateFlow<LoadingState<Any>> = _loadingState.asStateFlow()

    val isNetworkAvailable: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _databaseIsEmpty = MutableStateFlow(true)
    val databaseIsEmpty = _databaseIsEmpty.asStateFlow()

    private val _offlineMode = MutableStateFlow(false)
    val offlineMode: StateFlow<Boolean> = _offlineMode.asStateFlow()

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
                    _offlineMode.value = false
                    _loadingState.value = LoadingState.Success(Any())
                } else {
                    _loadingState.value = LoadingState.Loading
                    delay(4000)
                    _offlineMode.value = false
                    val message = "No internet connection"
                    if(authRepository.isSignedIn()){
                        if(databaseIsEmpty.value){
                            _offlineMode.value = false
                            _loadingState.value = LoadingState.Error(message)
                        } else {
                            _offlineMode.value = true
                            _loadingState.value = LoadingState.Success(Any())
                        }
                    } else{
                        _offlineMode.value = false
                        _loadingState.value = LoadingState.Error(message)
                    }
                }
            }
        }
    }

    fun isSignedIn(): Boolean {
        return authRepository.isSignedIn()
    }

    fun updateDatabaseIsEmpty(value: Boolean) {
        _databaseIsEmpty.value = value
    }

}