package uk.ac.tees.mad.stox.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import uk.ac.tees.mad.stox.R
import uk.ac.tees.mad.stox.model.dataclass.splashscreen.LoadingState
import uk.ac.tees.mad.stox.view.navigation.Dest
import uk.ac.tees.mad.stox.view.navigation.SubGraph
import uk.ac.tees.mad.stox.view.utils.LoadingErrorScreen
import uk.ac.tees.mad.stox.viewmodel.MainViewModel
import uk.ac.tees.mad.stox.viewmodel.SplashScreenViewModel

@Composable
fun SplashScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    viewmodel: SplashScreenViewModel = koinViewModel()
) {
    val databaseIsEmpty by mainViewModel.databaseIsEmpty.collectAsStateWithLifecycle()

    val loadingState by viewmodel.loadingState.collectAsStateWithLifecycle()
    val offlineMode by viewmodel.offlineMode.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = databaseIsEmpty) {
        viewmodel.updateDatabaseIsEmpty(databaseIsEmpty)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = loadingState,
                animationSpec = tween(durationMillis = 1000),
                label = "splashScreen"
            ) { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        var startAnimation by remember { mutableStateOf(false) }
                        val alphaAnim = animateFloatAsState(
                            targetValue = if (startAnimation) 1f else 0f,
                            animationSpec = tween(durationMillis = 3000), label = ""
                        )
                        LaunchedEffect(key1 = true) {
                            startAnimation = true
                        }
                        Splash(alpha = alphaAnim.value)
                    }

                    is LoadingState.Error -> {
                        LoadingErrorScreen(errorMessage = state.message,
                            onRetry = { viewmodel.startLoading() })
                    }

                    is LoadingState.Success -> {
                        LaunchedEffect(key1 = Unit) {
                            if (viewmodel.isSignedIn()) {
                                if (offlineMode) {
                                    // Offline mode, user is signed in, navigate to home
                                    navController.navigate(SubGraph.HomeGraph) {
                                        popUpTo(Dest.SplashScreen) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    navController.navigate(SubGraph.HomeGraph) {
                                        popUpTo(Dest.SplashScreen) {
                                            inclusive = true
                                        }
                                    }
                                }
                            } else {
                                navController.navigate(SubGraph.AuthGraph) {
                                    popUpTo(Dest.SplashScreen) {
                                        inclusive = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Splash(alpha: Float) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier
                .size(120.dp)
                .alpha(alpha = alpha),
            painter = painterResource(id = R.drawable.stox),
            contentDescription = "App Logo"
        )
        Text(
            text = "Stox",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 10.dp)
                .alpha(alpha = alpha)
        )
    }
}