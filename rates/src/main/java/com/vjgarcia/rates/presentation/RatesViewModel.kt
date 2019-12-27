package com.vjgarcia.rates.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.vjgarcia.rates.domain.GetLatestRates
import com.vjgarcia.rates.domain.LatestRatesError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

internal class RatesViewModel(
    private val getLatestRates: GetLatestRates
) : ViewModel() {

    private val subscriptions = CompositeDisposable()

    private val mutableRatesState = MutableLiveData<RatesState>()
    val ratesState: LiveData<RatesState> = mutableRatesState

    fun onRatesVisible() {
        getLatestRates()
            .subscribeOn(Schedulers.computation())
            .map { latestRatesResult ->
                latestRatesResult.map { latestRates -> latestRates.toRatesState() }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onLatestRatesResult) { onLatestRatesError() }
            .let(subscriptions::add)
    }

    fun onRatesGone() {
        subscriptions.clear()
    }

    private fun onLatestRatesResult(latestRatesResult: Either<LatestRatesError, RatesState>) {
        latestRatesResult.fold(
            ifLeft = { onLatestRatesError() },
            ifRight = ::onLatestRatesSuccess
        )
    }

    private fun onLatestRatesSuccess(ratesState: RatesState) {
        mutableRatesState.value = ratesState
    }

    private fun onLatestRatesError() {
        Log.d(LOG_TAG, "error fetching latest rates")
    }

    private companion object {
        const val LOG_TAG = "RatesViewModel"
    }
}