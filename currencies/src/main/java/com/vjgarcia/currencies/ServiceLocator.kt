package com.vjgarcia.currencies

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vjgarcia.currencies.data.network.RatesApiClient
import com.vjgarcia.currencies.data.network.RevolutApiConfig
import com.vjgarcia.currencies.data.network.RevolutHttpUrlFactory
import com.vjgarcia.currencies.data.repository.RatesRepository
import com.vjgarcia.currencies.domain.*
import com.vjgarcia.currencies.presentation.viewmodel.CurrenciesViewModelFactory
import okhttp3.OkHttpClient

object ServiceLocator {

    val currenciesViewModelFactory
        get() = CurrenciesViewModelFactory(
            getLatestRates,
            updateBaseCurrency,
            updateCurrentAmount
        )

    private val getLatestRates
        get() = GetCurrencies(ratesBusinessLogic)

    private val updateBaseCurrency
        get() = UpdateBaseCurrency(ratesBusinessLogic)

    private val updateCurrentAmount
        get() = UpdateCurrentAmount(ratesBusinessLogic)

    private val currenciesReducer
        get() = CurrenciesReducer()

    private val ratesBusinessLogic by lazy { CurrenciesBusinessLogic(ratesRepository, currenciesReducer) }

    private val ratesRepository
        get() = RatesRepository(ratesApiClient)

    private val ratesApiClient
        get() = RatesApiClient(
            okHttpClient,
            moshi,
            revolutApiConfig,
            revolutHttpUrlFactory
        )

    private val okHttpClient
        get() = OkHttpClient.Builder().build()

    private val moshi
        get() = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val revolutApiConfig
        get() = RevolutApiConfig(
            baseEndpoint = BuildConfig.API_ENDPOINT,
            latestRatesPath = BuildConfig.API_LATEST_RATES_PATH
        )

    private val revolutHttpUrlFactory
        get() = RevolutHttpUrlFactory()
}