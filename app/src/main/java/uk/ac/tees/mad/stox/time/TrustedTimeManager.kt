package uk.ac.tees.mad.stox.time

import android.util.Log
import com.google.android.gms.time.TrustedTimeClient
import kotlinx.coroutines.tasks.await

class TrustedTimeManager(private val trustedTimeClientAccessor: TrustedTimeClientAccessor) {

    private var trustedTimeClient: TrustedTimeClient? = null
    var isInitialized: Boolean = false
        private set

    suspend fun initialize() {
        try {
            trustedTimeClient = trustedTimeClientAccessor.createClient().await()
            isInitialized = true
        } catch (e: Exception) {
            Log.e("TrustedTimeManager", "Error initializing TrustedTimeClient", e)
            isInitialized = false
        }
    }

    fun getCurrentTimeInMillis(): Long? {
        return if (isInitialized) {
            trustedTimeClient?.computeCurrentUnixEpochMillis()
        } else {
            null
        }
    }
}