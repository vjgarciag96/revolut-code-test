package com.vjgarcia.rates.data.repository

import arrow.core.Either
import com.vjgarcia.rates.data.network.RatesApiClient
import io.reactivex.Single

internal class RatesRepository(
    private val ratesApiClient: RatesApiClient
) {

    fun latestRatesSingle(): Single<LatestRatesData> = Single.create {singleEmitter ->
        ratesApiClient.latestRates()
            .mapLeft {  }
            .map { singleEmitter.onSuccess(it.toDataModel()) }
    }

    fun latestRates(): Either<LatestRatesDataError, LatestRatesData> =
        ratesApiClient.latestRates()
            .mapLeft { LatestRatesDataError }
            .map { it.toDataModel() }

}