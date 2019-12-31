package com.vjgarcia.rates.presentation

internal interface RatesEventsListener {
    fun onAmountChanged(amount: CharSequence)
    fun onRowClicked(row: CurrencyRow)
}