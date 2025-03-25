package uk.ac.tees.mad.stox.view.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import uk.ac.tees.mad.stox.model.time.TrustedTimeManager
import uk.ac.tees.mad.stox.ui.screens.DetailsScreen
import uk.ac.tees.mad.stox.ui.screens.HomeScreen
import uk.ac.tees.mad.stox.ui.screens.ProfileScreen
import uk.ac.tees.mad.stox.ui.screens.SearchScreen
import uk.ac.tees.mad.stox.ui.screens.SignInScreen
import uk.ac.tees.mad.stox.ui.screens.SignUpScreen
import uk.ac.tees.mad.stox.ui.screens.SplashScreen

const val CARD_TRANSITION_KEY = "CARD_TRANSITION_KEY"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    trustedTimeManager: TrustedTimeManager,
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController, startDestination = Dest.SplashScreen
        ) {
            composable<Dest.SplashScreen> {
                SplashScreen(navController = navController)
            }
            navigation<SubGraph.AuthGraph>(startDestination = Dest.SignInScreen) {
                composable<Dest.SignInScreen> {
                    SignInScreen(navController = navController, animatedVisibilityScope = this)
                }
                composable<Dest.SignUpScreen> {
                    SignUpScreen(navController = navController, animatedVisibilityScope = this)
                }
            }
            navigation<SubGraph.HomeGraph>(startDestination = Dest.HomeScreen) {
                composable<Dest.HomeScreen> {
                    HomeScreen(navController = navController)
                }
                composable<Dest.SearchScreen> {
                    SearchScreen(navController = navController)
                }
                composable<Dest.DetailsScreen> {
                    val args = it.toRoute<Dest.DetailsScreen>()
                    DetailsScreen(
                        navController = navController, symbol = args.symbol
                    )
                }
                composable<Dest.ProfileScreen> {
                    ProfileScreen(navController = navController)
                }
            }
        }
    }
}