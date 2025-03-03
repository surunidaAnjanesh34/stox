package uk.ac.tees.mad.stox.model.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class NetworkConnectivityManager(private val context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observeConnectivity(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                launch { send(true) }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                launch { send(false) }
            }
        }

        val request =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        // Check initial state
        val isCurrentlyConnected = connectivityManager.activeNetwork?.let {
            val capabilities = connectivityManager.getNetworkCapabilities(it)
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } == true
        launch { send(isCurrentlyConnected) }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged()
}