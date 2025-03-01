package uk.ac.tees.mad.stox.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import uk.ac.tees.mad.stox.time.TrustedTimeManager
import uk.ac.tees.mad.stox.ui.screens.SplashScreen

@Composable
fun SetupNavGraph(navController: NavHostController, trustedTimeManager: TrustedTimeManager) {
    NavHost(
        navController = navController, startDestination = Dest.SplashScreen
    ) {
        composable<Dest.SplashScreen> {
            SplashScreen(navController = navController, trustedTimeManager = trustedTimeManager)
        }
//        navigation<SubGraph.AuthGraph>(startDestination = Dest.SignInScreen) {
//            composable<Dest.SignInScreen> {
//                SignInScreen(navController = navController)
//            }
//            composable<Dest.SignUpScreen> {
//                SignUpScreen(navController = navController)
//            }
//        }
//        navigation<SubGraph.HomeGraph>(startDestination = Dest.HomeScreen) {
//            composable<Dest.HomeScreen> {
//                HomeScreen(navController = navController)
//            }
//            composable<Dest.NowPlayingScreen> {
//                val args = it.toRoute<Dest.NowPlayingScreen>()
//                NowPlayingScreen(
//                    navController = navController,
//                    trackId = args.trackId,
//                    flag = args.flag
//                )
//            }
//            composable<Dest.PlaylistScreen> {
//                PlaylistScreen(
//                    navController = navController
//                )
//            }
//            composable<Dest.ProfileScreen> {
//                ProfileScreen(navController = navController)
//            }
//        }
    }
}