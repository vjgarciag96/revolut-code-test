package com.vjgarcia.rates.presentation

import com.vjgarcia.currencyflags.CurrencyFlags
import com.vjgarcia.rates.domain.CurrenciesState
import com.vjgarcia.rates.domain.Currency
import com.vjgarcia.rates.domain.CurrencyDecimal

fun CurrenciesState.toViewState(): CurrenciesViewState = when (this) {
    CurrenciesState.Initializing -> CurrenciesViewState.Loading
    is CurrenciesState.Content -> toViewState()
}

fun CurrenciesState.Content.toViewState(): CurrenciesViewState.Content =
    CurrenciesViewState.Content(currencies.mapIndexed { index, currency ->
        currency.toCurrencyRow(amount, baseCurrency.rate, index == 0)
    })

fun Currency.toCurrencyRow(
    amount: CurrencyDecimal,
    baseRate: CurrencyDecimal,
    editingEnabled: Boolean
): CurrencyRow = CurrencyRow(
    id = code,
    title = code,
    subtitle = java.util.Currency.getInstance(code).displayName,
    currencyFlagResId = CurrencyFlags.byISOCode[code],
    relativeAmount = (amount.divide(baseRate)).multiply(rate),
    editingEnabled = editingEnabled
)