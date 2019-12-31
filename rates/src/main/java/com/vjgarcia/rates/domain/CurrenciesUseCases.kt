package com.vjgarcia.rates.domain

import android.util.Log
import io.reactivex.Observable
import java.math.BigDecimal

internal class GetCurrencies(
    private val currenciesBusinessLogic: CurrenciesBusinessLogic
) {

    operator fun invoke(): Observable<CurrenciesState> = currenciesBusinessLogic.currenciesStream()
}

internal class UpdateBaseCurrency(
    private val currenciesBusinessLogic: CurrenciesBusinessLogic
) {

    operator fun invoke(newBaseCurrencyCode: String) {
        currenciesBusinessLogic.onCurrencyChanged(newBaseCurrencyCode)
    }
}

internal class UpdateCurrentAmount(
    private val currenciesBusinessLogic: CurrenciesBusinessLogic
) {

    operator fun invoke(newCurrentAmount: String) {
        Log.d(TAG, "update current baseAmount $newCurrentAmount")
        currenciesBusinessLogic.onAmountChanged(CurrencyDecimal.fromString(newCurrentAmount))
    }

    private companion object {
        const val TAG = "UpdateCurrentAmount"
    }
}