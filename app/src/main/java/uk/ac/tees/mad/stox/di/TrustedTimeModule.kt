package uk.ac.tees.mad.stox.di

import com.google.android.gms.tasks.Task
import com.google.android.gms.time.TrustedTime
import com.google.android.gms.time.TrustedTimeClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import uk.ac.tees.mad.stox.model.time.TrustedTimeClientAccessor
import uk.ac.tees.mad.stox.model.time.TrustedTimeManager

val trustedTimeModule = module {
    single<TrustedTimeClientAccessor> {
        object : TrustedTimeClientAccessor {
            override fun createClient(): Task<TrustedTimeClient> {
                return TrustedTime.createClient(androidContext())
            }
        }
    }
    single { TrustedTimeManager(get()) }
}