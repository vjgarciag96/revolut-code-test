package com.vjgarcia.currencies.data.repository

import arrow.core.Either
import com.vjgarcia.currencies.data.network.RatesApiClient

class RatesRepository(
    private val ratesApiClient: RatesApiClient
) {

    fun latestRates(): Either<LatestRatesDataError, LatestRatesData> =
        ratesApiClient.latestRates()
            .mapLeft { LatestRatesDataError }
            .map { it.toDataModel() }
}