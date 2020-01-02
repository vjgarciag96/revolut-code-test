package com.vjgarcia.currencies.domain

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CurrenciesReducerTest {

    private val sut = CurrenciesReducer()

    @Test
    internal fun `Initializing state + BaseCurrencyUpdated event = exception thrown`() {
        assertThrows(IllegalStateException::class.java) {
            sut.reduce(
                CurrenciesEvent.BaseCurrencyUpdated("EUR"),
                CurrenciesState.Initializing
            )
        }
    }

    @Test
    internal fun `Initializing state + BaseAmountUpdated event = exception thrown`() {
        assertThrows(IllegalStateException::class.java) {
            sut.reduce(
                CurrenciesEvent.BaseAmountUpdated(ANY_AMOUNT),
                CurrenciesState.Initializing
            )
        }
    }

    @Test
    internal fun `Initializing state + RatesUpdated event = Content state with rates from event`() {
        val newState = sut.reduce(anyRatesUpdatedEvent(), CurrenciesState.Initializing)

        val expectedState = CurrenciesState.Content(
            amount = CurrencyDecimal.fromDouble(100.00),
            baseCurrency = Currency("USD", CurrencyDecimal.fromDouble(1.00)),
            currencies = listOf(
                Currency("USD", CurrencyDecimal.fromDouble(1.00)),
                Currency("EUR", CurrencyDecimal.fromDouble(1.23)),
                Currency("GBP", CurrencyDecimal.fromDouble(2.13))
            )
        )
        assertEquals(expectedState, newState)
    }

    @Test
    internal fun `Content state + RatesUpdated event = new Content state with rates updated`() {
        val newState = sut.reduce(anyRatesUpdatedEvent(), anyContentState())

        val expectedBaseCurrency = anyBaseCurrency().copy(rate = CurrencyDecimal.fromDouble(1.23))
        val expectedState = CurrenciesState.Content(
            amount = ANY_AMOUNT,
            baseCurrency = expectedBaseCurrency,
            currencies = listOf(
                expectedBaseCurrency,
                Currency("USD", CurrencyDecimal.fromDouble(1.00)),
                Currency("GBP", CurrencyDecimal.fromDouble(2.13))
            )
        )
        assertEquals(expectedState, newState)
    }

    @Test
    internal fun `Content state + BaseAmountUpdated event = new Content state with amount updated`() {
        val newState = sut.reduce(CurrenciesEvent.BaseAmountUpdated(ANY_OTHER_AMOUNT), anyContentState())

        val expectedState = anyContentState().copy(amount = ANY_OTHER_AMOUNT)
        assertEquals(expectedState, newState)
    }

    @Test
    internal fun `Content state + BaseCurrencyUpdated event = new Content state with base currency updated`() {
        val newState = sut.reduce(CurrenciesEvent.BaseCurrencyUpdated(gbpCurrency().code), anyContentState())

        val expectedState = CurrenciesState.Content(
            amount = CurrencyDecimal.fromDouble(104.3478260869565),
            baseCurrency = gbpCurrency(),
            currencies = listOf(
                gbpCurrency(),
                eurCurrency(),
                usdCurrency()
            )
        )
        assertEquals(expectedState, newState)
    }

    @Test
    internal fun `Initializing state + RatesUpdatedError = Error state`() {
        val newState = sut.reduce(CurrenciesEvent.RatesUpdatedError, CurrenciesState.Initializing)

        assertEquals(CurrenciesState.Error, newState)
    }

    @Test
    internal fun `Content state + RatesUpdatedError = same Content state`() {
        val contentState = anyContentState()
        val newState = sut.reduce(CurrenciesEvent.RatesUpdatedError, contentState)

        assertEquals(contentState, newState)
    }

    private fun anyRatesUpdatedEvent(): CurrenciesEvent.RatesUpdated = CurrenciesEvent.RatesUpdated(anyLatestRates())

    private fun anyContentState(): CurrenciesState.Content =
        CurrenciesState.Content(ANY_AMOUNT, anyBaseCurrency(), anyCurrencies())

    private fun anyLatestRates(): LatestRates = LatestRates(
        base = "USD",
        byCurrencyCode = mapOf(
            "EUR" to CurrencyDecimal.fromDouble(1.23),
            "GBP" to CurrencyDecimal.fromDouble(2.13)
        )
    )

    private fun anyCurrencies(): List<Currency> = listOf(
        anyBaseCurrency(),
        usdCurrency(),
        gbpCurrency()
    )

    private fun anyBaseCurrency(): Currency = eurCurrency()

    private fun eurCurrency(): Currency = Currency("EUR", CurrencyDecimal.fromDouble(1.15))

    private fun usdCurrency(): Currency = Currency("USD", CurrencyDecimal.fromDouble(1.00))

    private fun gbpCurrency(): Currency = Currency("GBP", CurrencyDecimal.fromDouble(1.2))

    private companion object {
        val ANY_AMOUNT = CurrencyDecimal.fromDouble(100.00)
        val ANY_OTHER_AMOUNT = CurrencyDecimal.fromDouble(1330.00)
    }
}