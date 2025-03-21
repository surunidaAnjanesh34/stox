package uk.ac.tees.mad.stox.di

import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import uk.ac.tees.mad.stox.model.network.NetworkConnectivityManager
import uk.ac.tees.mad.stox.model.repository.AlphaVantageRepository
import uk.ac.tees.mad.stox.model.repository.AuthRepository
import uk.ac.tees.mad.stox.model.repository.HomeScreenStockDataRepository
import uk.ac.tees.mad.stox.model.repository.NetworkRepository
import uk.ac.tees.mad.stox.model.retrofit.AlphaVantageRetrofitInstance
import uk.ac.tees.mad.stox.model.room.HomeScreenStockDataItemTypeConverter
import uk.ac.tees.mad.stox.model.room.StoxDatabase
import uk.ac.tees.mad.stox.model.serviceapi.alphaVantageApiService
import uk.ac.tees.mad.stox.viewmodel.DetailsScreenViewModel
import uk.ac.tees.mad.stox.viewmodel.HomeScreenViewModel
import uk.ac.tees.mad.stox.viewmodel.SearchScreenViewModel
import uk.ac.tees.mad.stox.viewmodel.SignInScreenViewModel
import uk.ac.tees.mad.stox.viewmodel.SignUpScreenViewModel
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

    // Alpha Vantage
    single<alphaVantageApiService> { AlphaVantageRetrofitInstance.create() }
    single { AlphaVantageRepository(get()) }

    // Stox Database
    // Home Screen Stock Data
    singleOf(::HomeScreenStockDataItemTypeConverter)
    single {
        Room.databaseBuilder(
            androidApplication(), StoxDatabase::class.java, "stox_database"
        ).addTypeConverter(HomeScreenStockDataItemTypeConverter()).build()
    }
    single {
        val database = get<StoxDatabase>()
        database.homeScreenStockDataDao()
    }
    single { HomeScreenStockDataRepository(get()) }

    // ViewModels
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::SignInScreenViewModel)
    viewModelOf(::SignUpScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::SearchScreenViewModel)
    viewModelOf(::DetailsScreenViewModel)
}