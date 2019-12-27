package com.vjgarcia.rates.data.network

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

internal class RevolutHttpUrlFactory {

    fun createLatestRatesUrl(
        revolutApiConfig: RevolutApiConfig,
        base: String
    ): HttpUrl? {
        val httpUrl = "${revolutApiConfig.baseEndpoint}${revolutApiConfig.latestRatesPath}".toHttpUrlOrNull()

        return httpUrl?.newBuilder()?.apply {
            addQueryParameter(BASE_QUERY_PARAM_NAME, base)
        }?.build()
    }

    private companion object {
        const val BASE_QUERY_PARAM_NAME = "base"
    }
}