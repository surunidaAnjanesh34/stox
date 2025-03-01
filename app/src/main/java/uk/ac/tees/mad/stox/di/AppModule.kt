package uk.ac.tees.mad.stox.di

import org.koin.dsl.module

val appModule = module {
    includes(trustedTimeModule)

}