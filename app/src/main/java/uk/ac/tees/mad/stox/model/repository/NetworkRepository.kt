package uk.ac.tees.mad.stox.model.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.stox.model.network.NetworkConnectivityManager

class NetworkRepository(private val networkConnectivityManager: NetworkConnectivityManager) {
    val isNetworkAvailable: Flow<Boolean> = networkConnectivityManager.observeConnectivity()
}