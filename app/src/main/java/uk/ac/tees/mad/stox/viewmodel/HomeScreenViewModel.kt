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

class HomeScreenViewModel(private val alphaVantageRepository: AlphaVantageRepository) :
    ViewModel() {
    private val _globalQuoteState =
        MutableStateFlow<LoadingState<GlobalQuote>>(LoadingState.Loading)
    val globalQuoteState: StateFlow<LoadingState<GlobalQuote>> = _globalQuoteState.asStateFlow()

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