package uk.ac.tees.mad.stox.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.stox.model.network.NetworkConnectivityManager
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository
import uk.ac.tees.mad.stox.viewmodel.MainViewModel
import uk.ac.tees.mad.stox.viewmodel.SplashScreenViewModel

val appModule = module {
    // TrustedTime API
    includes(trustedTimeModule)

    // Network
    single { NetworkConnectivityManager(androidContext()) }
    single { NetworkRepository(get()) }

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { AuthRepository(get()) }

    // ViewModels
    viewModelOf(::MainViewModel)
    viewModelOf(::SplashScreenViewModel)
}