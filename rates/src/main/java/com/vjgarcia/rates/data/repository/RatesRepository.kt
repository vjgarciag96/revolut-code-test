package com.vjgarcia.rates.data.repository

import arrow.core.Either
import com.vjgarcia.rates.data.network.RatesApiClient

internal class RatesRepository(
    private val ratesApiClient: RatesApiClient
) {

    fun latestRates(): Either<LatestRatesDataError, LatestRatesData> =
        ratesApiClient.latestRates()
            .mapLeft { LatestRatesDataError }
            .map { it.toDataModel() }
}