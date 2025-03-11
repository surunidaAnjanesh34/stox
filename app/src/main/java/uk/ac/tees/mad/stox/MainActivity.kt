package uk.ac.tees.mad.stox

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import uk.ac.tees.mad.stox.model.time.TrustedTimeManager
import uk.ac.tees.mad.stox.ui.theme.StoxTheme
import uk.ac.tees.mad.stox.view.navigation.SetupNavGraph
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val trustedTimeManager: TrustedTimeManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
//            setKeepOnScreenCondition {
//                // Condition to check if the app is still loading and showing the splash screen or not.
//                TODO()
//            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_X, 0.4f, 0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }
                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_Y, 0.4f, 0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
        enableEdgeToEdge()
        CoroutineScope(Dispatchers.Main).launch {
            trustedTimeManager.initialize()
            val currentTime = trustedTimeManager.getCurrentTimeInMillis()
            if (currentTime != null) {
                Log.d("MainActivity", "Current Time: $currentTime")
            } else {
                Log.e("MainActivity", "Failed to get trusted time.")
            }
        }
        setContent {
            StoxTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController = navController, trustedTimeManager = trustedTimeManager
                )
            }
        }
    }
}
