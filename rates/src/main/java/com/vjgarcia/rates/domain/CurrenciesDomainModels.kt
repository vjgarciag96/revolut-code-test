package com.vjgarcia.rates.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class CurrencyDecimal private constructor(val value: BigDecimal) {

    private object Constants {
        val mathContext = MathContext(4, RoundingMode.HALF_EVEN)
    }

    fun multiply(currencyDecimal: CurrencyDecimal): CurrencyDecimal =
        CurrencyDecimal(value.multiply(currencyDecimal.value, Constants.mathContext))

    fun divide(currencyDecimal: CurrencyDecimal): CurrencyDecimal =
        CurrencyDecimal(value.divide(currencyDecimal.value, Constants.mathContext))

    fun toPlainString(): String = value.toPlainString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CurrencyDecimal

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {
        fun fromDouble(double: Double): CurrencyDecimal = CurrencyDecimal(
            BigDecimal(double, Constants.mathContext)
        )

        fun fromString(string: String): CurrencyDecimal = CurrencyDecimal(
            BigDecimal(string, Constants.mathContext)
        )
    }
}

sealed class CurrenciesEvent {
    data class RatesUpdated(val latestRates: LatestRates) : CurrenciesEvent()
    data class BaseCurrencyUpdated(val newBaseCurrency: String) : CurrenciesEvent()
    data class BaseAmountUpdated(val newBaseAmount: CurrencyDecimal) : CurrenciesEvent()
}

sealed class CurrenciesState {
    object Initializing : CurrenciesState()
    data class Content(
        val amount: CurrencyDecimal,
        val baseCurrency: Currency,
        val currencies: List<Currency>
    ) : CurrenciesState()
}

data class LatestRates(
    val base: String,
    val byCurrencyCode: Map<String, CurrencyDecimal>
)

data class Currency(
    val code: String,
    val rate: CurrencyDecimal
)