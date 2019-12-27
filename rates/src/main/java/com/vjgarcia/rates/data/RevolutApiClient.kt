package com.vjgarcia.rates.data

import arrow.core.Either
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

internal class RevolutApiClient(
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi,
    private val revolutApiConfig: RevolutApiConfig,
    private val revolutHttpUrlFactory: RevolutHttpUrlFactory
) {

    fun latestRates(base: String = EUR_CURRENCY_CODE): Either<LatestRatesError, LatestRatesDTO> {
        val latestRatesUrl = revolutHttpUrlFactory.createLatestRatesUrl(revolutApiConfig, base)

        if (latestRatesUrl == null) {
            return Either.left(LatestRatesError)
        }

        val latestRatesRequest = Request.Builder()
            .url(latestRatesUrl)
            .build()

        try {
            val latestRatesResponse = okHttpClient.newCall(latestRatesRequest).execute()

            if (!latestRatesResponse.isSuccessful) {
                return Either.left(LatestRatesError)
            }

            val latestRatesJsonBody = latestRatesResponse.body?.string()

            if (latestRatesJsonBody == null) {
                return Either.left(LatestRatesError)
            }

            val latestRatesDtoAdapter = moshi.adapter(LatestRatesDTO::class.java)
            val latestRatesDto = latestRatesDtoAdapter.fromJson(latestRatesJsonBody)

            if (latestRatesDto == null) {
                return Either.left(LatestRatesError)
            }

            return Either.right(latestRatesDto)
        } catch (ioException: IOException) {
            return Either.left(LatestRatesError)
        }
    }


    private companion object {
        const val EUR_CURRENCY_CODE = "EUR"
    }
}