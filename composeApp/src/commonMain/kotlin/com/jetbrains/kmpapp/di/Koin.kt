package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.data.githubsearch.GithubApi
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchViewModel
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingViewModel
import com.jetbrains.kmpapp.data.museum.InMemoryMuseumStorage
import com.jetbrains.kmpapp.data.museum.KtorMuseumApi
import com.jetbrains.kmpapp.data.museum.MuseumApi
import com.jetbrains.kmpapp.data.museum.MuseumRepository
import com.jetbrains.kmpapp.data.museum.MuseumStorage
import com.jetbrains.kmpapp.screens.museum.detail.DetailViewModel
import com.jetbrains.kmpapp.screens.museum.list.ListViewModel
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {
    // アプリ共通で利用するJsonインスタンスを提供する (single: シングルトンインスタンス)
    single<Json> { Json { ignoreUnknownKeys = true } }

    // Museum関連のDI設定
    single<MuseumApi> { KtorMuseumApi(get()) }
    single<MuseumStorage> { InMemoryMuseumStorage() }
    single {
        MuseumRepository(get(), get()).apply {
            initialize()
        }
    }

    // GithubAPI関連のDI設定
    single<GithubApi> { GithubApi(get()) }
}

val viewModelModule = module {
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
    factoryOf(::GithubSearchViewModel)
    factoryOf(::GithubSearchPagingViewModel)
}

fun initKoin() {
    startKoin { modules(dataModule, viewModelModule) }
}
