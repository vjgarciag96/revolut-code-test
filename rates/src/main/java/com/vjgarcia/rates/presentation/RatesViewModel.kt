package com.vjgarcia.rates.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vjgarcia.rates.domain.GetCurrencies
import com.vjgarcia.rates.domain.UpdateBaseCurrency
import com.vjgarcia.rates.domain.UpdateCurrentAmount
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

internal class RatesViewModel(
    private val getCurrencies: GetCurrencies,
    private val updateBaseCurrency: UpdateBaseCurrency,
    private val updateCurrentAmount: UpdateCurrentAmount
) : ViewModel(), RatesEventsListener {

    private val subscriptions = CompositeDisposable()

    private val mutableCurrenciesState = MutableLiveData<CurrenciesViewState>(CurrenciesViewState.Loading)
    val currenciesState: LiveData<CurrenciesViewState> = mutableCurrenciesState

    private val mutableCurrenciesEffect = MutableLiveData<CurrenciesEffect>()
    val currenciesEffect: LiveData<CurrenciesEffect> = mutableCurrenciesEffect

    fun onRatesVisible() {
        getCurrencies()
            .subscribeOn(Schedulers.computation())
            .map { currenciesState -> currenciesState.toViewState() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onCurrenciesViewState) {
                onGetCurrenciesError()
            }
            .let(subscriptions::add)
    }

    fun onRatesGone() {
        subscriptions.clear()
    }

    override fun onAmountChanged(amount: CharSequence) {
        if (amount.isEmpty() || amount.isBlank()) {
            updateCurrentAmount("0")
            return
        }
        updateCurrentAmount(amount.toString())
    }

    override fun onRowClicked(row: CurrencyRow) {
        updateBaseCurrency(row.id)
        mutableCurrenciesEffect.value = CurrenciesEffect.ScrollToTop
    }

    private fun onCurrenciesViewState(currenciesViewState: CurrenciesViewState) {
        mutableCurrenciesState.value = currenciesViewState
    }

    private fun onGetCurrenciesError() {
        Log.d(LOG_TAG, "error fetching latest currencies")
    }

    private companion object {
        const val LOG_TAG = "RatesViewModel"
    }
}