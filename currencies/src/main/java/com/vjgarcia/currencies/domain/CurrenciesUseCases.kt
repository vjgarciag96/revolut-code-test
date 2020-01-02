package com.vjgarcia.currencies.domain

import android.util.Log
import io.reactivex.Observable

class GetCurrencies(
    private val currenciesBusinessLogic: CurrenciesBusinessLogic
) {

    operator fun invoke(): Observable<CurrenciesState> = currenciesBusinessLogic.currenciesStream()
}

class UpdateBaseCurrency(
    private val currenciesBusinessLogic: CurrenciesBusinessLogic
) {

    operator fun invoke(newBaseCurrencyCode: String) {
        currenciesBusinessLogic.onCurrencyChanged(newBaseCurrencyCode)
    }
}

class UpdateCurrentAmount(
    private val currenciesBusinessLogic: CurrenciesBusinessLogic
) {

    operator fun invoke(newCurrentAmount: String) {
        Log.d(TAG, "update current baseAmount $newCurrentAmount")
        if (newCurrentAmount.isBlank() || newCurrentAmount.isEmpty()) {
            currenciesBusinessLogic.onAmountChanged()
            return
        }

        currenciesBusinessLogic.onAmountChanged(CurrencyDecimal.fromString(newCurrentAmount))
    }

    private companion object {
        const val TAG = "UpdateCurrentAmount"
    }
}