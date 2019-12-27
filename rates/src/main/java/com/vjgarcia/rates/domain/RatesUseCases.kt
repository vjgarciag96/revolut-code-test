package com.vjgarcia.rates.domain

import arrow.core.Either
import com.vjgarcia.rates.data.repository.RatesRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

internal class GetLatestRates(
    private val ratesRepository: RatesRepository
) {

    operator fun invoke(): Observable<Either<LatestRatesError, LatestRates>> =
        Observable.interval(1L, TimeUnit.SECONDS)
            .map {
                ratesRepository.latestRates()
                    .mapLeft { LatestRatesError }
                    .map { it.toDomain() }
            }.subscribeOn(Schedulers.io())
}