package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.AppViewModel
import com.jetbrains.kmpapp.data.InMemoryMuseumStorage
import com.jetbrains.kmpapp.data.KtorMuseumApi
import com.jetbrains.kmpapp.data.MuseumApi
import com.jetbrains.kmpapp.data.MuseumRepository
import com.jetbrains.kmpapp.data.MuseumStorage
import com.jetbrains.kmpapp.data.githubsearch.GithubApi
import com.jetbrains.kmpapp.data.pokemon.PokemonApi
import com.jetbrains.kmpapp.data.settings.SettingsDataStore
import com.jetbrains.kmpapp.screens.detail.DetailViewModel
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchViewModel
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingViewModel
import com.jetbrains.kmpapp.screens.list.ListViewModel
import com.jetbrains.kmpapp.screens.pokemon.PokemonPagingViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dataModule = module {
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
    single { GithubApi(get()) }
    single { PokemonApi(get()) }

    // 各種DataStoreのDI設定
    single { SettingsDataStore() }

    // CoroutineScopeのDI設定 - Unit Testを書く際にDispatchersを切り替えるために必要 - START
    single<CoroutineDispatcher>(named("IoDispatcher")) { Dispatchers.IO }
    single<CoroutineDispatcher>(named("TestDispatcher")) { Dispatchers.Main } // Unitテスト用のDispatcherを提供
    single<CoroutineScope>(named("IoCoroutineScope")) {
        CoroutineScope(get<CoroutineDispatcher>(named("IoDispatcher")) + SupervisorJob())
    }
    single<CoroutineScope>(named("TestCoroutineScope")) {
        CoroutineScope(get<CoroutineDispatcher>(named("TestDispatcher")) + SupervisorJob())
    }
    // CoroutineScopeのDI設定 - Unit Testを書く際にDispatchersを切り替えるために必要 - END
}

internal val viewModelModule = module {
    factoryOf(::ListViewModel)
    factoryOf(::DetailViewModel)
    factoryOf(::GithubSearchViewModel)
    factoryOf(::GithubSearchPagingViewModel)
    factoryOf(::AppViewModel)
    factoryOf(::PokemonPagingViewModel)
}

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
