package com.vjgarcia.currencies.domain

import com.vjgarcia.currencies.data.repository.RatesRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class CurrenciesBusinessLogic(
    private val ratesRepository: RatesRepository,
    private val currenciesReducer: CurrenciesReducer
) {

    private var currenciesState: CurrenciesState = CurrenciesState.Initializing

    private val latestRatesObservable = Observable.interval(0L, 1L, TimeUnit.SECONDS).map {
        ratesRepository.latestRates().fold(
            ifLeft = { CurrenciesEvent.RatesUpdatedError },
            ifRight = { CurrenciesEvent.RatesUpdated(it.toDomainModel()) }
        )
    }
    private val currenciesEventSubject = PublishSubject.create<CurrenciesEvent>()

    fun onCurrencyChanged(newBase: String) {
        currenciesEventSubject.onNext(CurrenciesEvent.BaseCurrencyUpdated(newBase))
    }

    fun onAmountChanged(newAmount: CurrencyDecimal? = null) {
        currenciesEventSubject.onNext(CurrenciesEvent.BaseAmountUpdated(newAmount))
    }

    fun currenciesStream(): Observable<CurrenciesState> =
        Observable.merge(currenciesEventSubject, latestRatesObservable).map { event ->
            currenciesReducer.reduce(event, currenciesState)
        }.doOnNext { newState ->
            currenciesState = newState
        }.distinctUntilChanged()
}