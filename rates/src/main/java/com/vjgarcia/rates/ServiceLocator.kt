package com.vjgarcia.rates

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.vjgarcia.rates.data.network.RatesApiClient
import com.vjgarcia.rates.data.network.RevolutApiConfig
import com.vjgarcia.rates.data.network.RevolutHttpUrlFactory
import com.vjgarcia.rates.data.repository.RatesRepository
import com.vjgarcia.rates.domain.GetLatestRates
import com.vjgarcia.rates.presentation.RatesViewModelFactory
import okhttp3.OkHttpClient

internal object ServiceLocator {

    val ratesViewModelFactory
        get() = RatesViewModelFactory(getLatestRates)

    val getLatestRates
        get() = GetLatestRates(ratesRepository)

    val ratesRepository by lazy { RatesRepository(ratesApiClient) }

    val ratesApiClient
        get() = RatesApiClient(
            okHttpClient,
            moshi,
            revolutApiConfig,
            revolutHttpUrlFactory
        )

    val okHttpClient
        get() = OkHttpClient.Builder().build()

    val moshi
        get() = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val revolutApiConfig
        get() = RevolutApiConfig(
            baseEndpoint = BuildConfig.API_ENDPOINT,
            latestRatesPath = BuildConfig.API_LATEST_RATES_PATH
        )

    val revolutHttpUrlFactory
        get() = RevolutHttpUrlFactory()
}