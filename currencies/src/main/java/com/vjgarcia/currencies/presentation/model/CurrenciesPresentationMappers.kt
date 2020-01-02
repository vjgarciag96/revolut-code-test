package com.vjgarcia.currencies.presentation.model

import com.vjgarcia.currencyflags.CurrencyFlags
import com.vjgarcia.currencies.domain.CurrenciesState
import com.vjgarcia.currencies.domain.Currency
import com.vjgarcia.currencies.domain.CurrencyDecimal

fun CurrenciesState.toViewState(): CurrenciesViewState = when (this) {
    CurrenciesState.Initializing -> CurrenciesViewState.Loading
    is CurrenciesState.Content -> toViewState()
    CurrenciesState.Error -> CurrenciesViewState.Error
}

fun CurrenciesState.Content.toViewState(): CurrenciesViewState.Content =
    CurrenciesViewState.Content(currencies.mapIndexed { index, currency ->
        if (index == 0) {
            currency.toBaseCurrencyRow(amount)
        } else {
            currency.toCurrencyRow(amount, baseCurrency.rate)
        }

    })

fun Currency.toBaseCurrencyRow(
    amount: CurrencyDecimal?
): CurrencyRow = CurrencyRow(
    id = code,
    title = code,
    subtitle = java.util.Currency.getInstance(code).displayName,
    currencyFlagResId = CurrencyFlags.byISOCode[code],
    relativeAmount = amount,
    editingEnabled = true
)

fun Currency.toCurrencyRow(
    amount: CurrencyDecimal?,
    baseRate: CurrencyDecimal
): CurrencyRow = CurrencyRow(
    id = code,
    title = code,
    subtitle = java.util.Currency.getInstance(code).displayName,
    currencyFlagResId = CurrencyFlags.byISOCode[code],
    relativeAmount = (amount?.divide(baseRate))?.multiply(rate),
    editingEnabled = false
)