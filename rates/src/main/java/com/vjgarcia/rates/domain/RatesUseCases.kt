package com.vjgarcia.rates.domain

import arrow.core.Either
import com.vjgarcia.rates.data.repository.RatesRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

internal class GetLatestRates(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(): Single<Either<LatestRatesError, LatestRates>> =
        Single.create<Either<LatestRatesError, LatestRates>> { emitter ->
            val latestRatesResult = ratesRepository.latestRates()
                .mapLeft { LatestRatesError }
                .map { it.toDomain() }

            emitter.onSuccess(latestRatesResult)
        }.subscribeOn(Schedulers.io())
}