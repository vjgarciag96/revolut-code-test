package com.vjgarcia.currencies.domain

class CurrenciesReducer {

    fun reduce(event: CurrenciesEvent, state: CurrenciesState): CurrenciesState =
        when {
            state is CurrenciesState.Initializing && event is CurrenciesEvent.RatesUpdated -> createFromNewRates(event)
            state is CurrenciesState.Initializing && event is CurrenciesEvent.RatesUpdatedError -> CurrenciesState.Error
            state is CurrenciesState.Content && event is CurrenciesEvent.RatesUpdated -> updateRates(state, event)
            state is CurrenciesState.Content && event is CurrenciesEvent.BaseAmountUpdated -> updateBaseAmount(state, event)
            state is CurrenciesState.Content && event is CurrenciesEvent.BaseCurrencyUpdated -> updateBaseCurrency(state, event)
            state is CurrenciesState.Content && event is CurrenciesEvent.RatesUpdatedError -> state
            else -> error("illegal combination of ($state, $event)")
        }

    private fun createFromNewRates(event: CurrenciesEvent.RatesUpdated): CurrenciesState {
        val baseCurrency = Currency(event.latestRates.base, BASE_RATE)

        val currencies = listOf(baseCurrency) + event.latestRates.byCurrencyCode.map { entry ->
            Currency(code = entry.key, rate = entry.value)
        }.sortedBy { it.code }

        return CurrenciesState.Content(
            amount = INITIAL_AMOUNT,
            baseCurrency = baseCurrency,
            currencies = currencies
        )
    }

    private fun updateRates(state: CurrenciesState.Content, event: CurrenciesEvent.RatesUpdated): CurrenciesState {
        val newCurrencies = state.currencies.map { currency ->
            val newRate = event.latestRates.byCurrencyCode[currency.code]
            return@map if (newRate == null) currency else currency.copy(rate = newRate)
        }

        val baseCurrency = state.baseCurrency
        val newBaseRate = event.latestRates.byCurrencyCode[baseCurrency.code]

        return if (newBaseRate != null && newBaseRate != baseCurrency.rate) {
            state.copy(
                baseCurrency = baseCurrency.copy(rate = newBaseRate),
                currencies = newCurrencies
            )
        } else {
            state.copy(currencies = newCurrencies)
        }
    }

    private fun updateBaseAmount(state: CurrenciesState.Content, event: CurrenciesEvent.BaseAmountUpdated): CurrenciesState =
        state.copy(amount = event.newBaseAmount)

    private fun updateBaseCurrency(state: CurrenciesState.Content, event: CurrenciesEvent.BaseCurrencyUpdated): CurrenciesState {
        val mutableCurrencies = state.currencies.toMutableList()

        val indexOfNewBaseCurrency = mutableCurrencies.indexOfFirst { it.code == event.newBaseCurrency }

        if (indexOfNewBaseCurrency == -1) {
            return state
        }

        val newBaseCurrency = mutableCurrencies.removeAt(indexOfNewBaseCurrency)
        val newAmount = (state.amount?.divide(state.baseCurrency.rate))?.multiply(newBaseCurrency.rate)

        return state.copy(
            amount = newAmount,
            baseCurrency = newBaseCurrency,
            currencies = listOf(newBaseCurrency).plus(mutableCurrencies)
        )
    }

    private companion object {
        val INITIAL_AMOUNT = CurrencyDecimal.fromDouble(100.00)
        val BASE_RATE = CurrencyDecimal.fromDouble(1.00)
    }
}