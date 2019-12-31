package com.vjgarcia.rates.domain

import android.util.Log
import com.vjgarcia.rates.data.repository.RatesRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal class CurrenciesBusinessLogic(
    private val ratesRepository: RatesRepository,
    private val currenciesReducer: CurrenciesReducer
) {

    private var currenciesState: CurrenciesState = CurrenciesState.Initializing

    private val latestRatesObservable = Observable.interval(0L, 1L, TimeUnit.SECONDS).map {
        ratesRepository.latestRates()
            .fold(ifLeft = { CurrenciesEvent.RatesUpdated(LatestRates("EUR", emptyMap())) },
                ifRight = { CurrenciesEvent.RatesUpdated(it.toDomainModel()) })
    }
    private val currenciesEventSubject = PublishSubject.create<CurrenciesEvent>()

    fun onCurrencyChanged(newBase: String) {
        Log.d(LOG_TAG, "onCurrencyChanged -> newBase = $newBase")
        currenciesEventSubject.onNext(CurrenciesEvent.BaseCurrencyUpdated(newBase))
    }

    fun onAmountChanged(newAmount: CurrencyDecimal) {
        currenciesEventSubject.onNext(CurrenciesEvent.BaseAmountUpdated(newAmount))
        Log.d(LOG_TAG, "onAmountChanged -> newAmount = $newAmount")
    }

    fun currenciesStream(): Observable<CurrenciesState> =
        Observable.merge(currenciesEventSubject, latestRatesObservable)
            .map { event ->
                currenciesReducer.reduce(event, currenciesState)
            }.doOnNext { newState ->
                currenciesState = newState
            }

    private companion object {
        const val LOG_TAG = "CurrenciesBusinessLogic"
    }
}