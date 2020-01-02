package com.vjgarcia.currencies.presentation.model

interface CurrenciesEventsListener {
    fun onAmountChanged(amount: CharSequence)
    fun onRowClicked(row: CurrencyRow)
}