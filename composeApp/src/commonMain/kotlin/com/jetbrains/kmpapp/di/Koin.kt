package com.jetbrains.kmpapp.di

import com.jetbrains.kmpapp.createDataStore
import com.jetbrains.kmpapp.data.githubsearch.GithubApi
import com.jetbrains.kmpapp.screens.githubsearch.GithubSearchViewModel
import com.jetbrains.kmpapp.screens.githubsearch.paging.GithubSearchPagingViewModel
import com.jetbrains.kmpapp.data.museum.InMemoryMuseumStorage
import com.jetbrains.kmpapp.data.museum.KtorMuseumApi
import com.jetbrains.kmpapp.data.museum.MuseumApi
import com.jetbrains.kmpapp.data.museum.MuseumRepository
import com.jetbrains.kmpapp.data.museum.MuseumStorage
import com.jetbrains.kmpapp.data.settings.ThemeDataStore
import com.jetbrains.kmpapp.screens.museum.detail.DetailViewModel
import com.jetbrains.kmpapp.screens.museum.list.ListViewModel
import com.jetbrains.kmpapp.screens.settings.SettingsViewModel
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
    single<GithubApi> { GithubApi(get()) }

    // 各種DataStoreのDI設定
    single { createDataStore() }
    single { ThemeDataStore(get()) }

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
    factoryOf(::SettingsViewModel)
}

fun initKoin() {
    startKoin { modules(dataModule, viewModelModule) }
}
