package uk.ac.tees.mad.stox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository

class MainViewModel(
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
): ViewModel()  {

    private val _databaseIsEmpty = MutableStateFlow(true)
    val databaseIsEmpty: StateFlow<Boolean> = _databaseIsEmpty.asStateFlow()

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

}