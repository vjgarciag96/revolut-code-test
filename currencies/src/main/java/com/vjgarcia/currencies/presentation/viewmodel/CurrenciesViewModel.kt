package com.vjgarcia.currencies.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vjgarcia.currencies.domain.GetCurrencies
import com.vjgarcia.currencies.domain.UpdateBaseCurrency
import com.vjgarcia.currencies.domain.UpdateCurrentAmount
import com.vjgarcia.currencies.presentation.model.*
import com.vjgarcia.currencies.presentation.model.CurrenciesEventsListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers

class CurrenciesViewModel(
    private val getCurrencies: GetCurrencies,
    private val updateBaseCurrency: UpdateBaseCurrency,
    private val updateCurrentAmount: UpdateCurrentAmount
) : ViewModel(), CurrenciesEventsListener {

    private var getCurrenciesSubscription = Disposables.empty()

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
                onCurrenciesViewState(CurrenciesViewState.Error)
            }.also { getCurrenciesSubscription = it }
    }

    fun onRatesGone() {
        getCurrenciesSubscription.dispose()
    }

    override fun onAmountChanged(amount: CharSequence) {
        updateCurrentAmount(amount.toString())
    }

    override fun onRowClicked(row: CurrencyRow) {
        updateBaseCurrency(row.id)
        mutableCurrenciesEffect.value = CurrenciesEffect.ScrollToTop
    }

    private fun onCurrenciesViewState(currenciesViewState: CurrenciesViewState) {
        mutableCurrenciesState.value = currenciesViewState
    }
}