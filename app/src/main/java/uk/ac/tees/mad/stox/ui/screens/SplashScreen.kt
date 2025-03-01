package uk.ac.tees.mad.stox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.inject
import uk.ac.tees.mad.stox.time.TrustedTimeManager
import kotlin.getValue

@Composable
fun SplashScreen(
    navController: NavHostController,
    trustedTimeManager: TrustedTimeManager
) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                var currentTime by remember { mutableStateOf<Long?>(null) }

                LaunchedEffect(key1 = true) {
                    while (true) {
                        currentTime = trustedTimeManager.getCurrentTimeInMillis()
                        delay(1000) // Update every 1 second
                    }
                }
                Text(text = "Hello, Current Time:$currentTime")
                if (currentTime != null) {
                    // Example: Displaying a formatted time
                    val formattedTime = java.text.SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(java.util.Date(
                        currentTime!!
                    ))
                    Text(text = "Formatted Time: $formattedTime")
                }
            }
        }
}