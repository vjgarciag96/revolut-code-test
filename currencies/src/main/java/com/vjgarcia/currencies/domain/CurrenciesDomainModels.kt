package com.vjgarcia.currencies.domain

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class CurrencyDecimal private constructor(val value: BigDecimal) {

    private object Constants {
        val mathContext = MathContext(16, RoundingMode.HALF_EVEN)
    }

    fun multiply(currencyDecimal: CurrencyDecimal): CurrencyDecimal =
        CurrencyDecimal(value.multiply(currencyDecimal.value, Constants.mathContext))

    fun divide(currencyDecimal: CurrencyDecimal): CurrencyDecimal =
        CurrencyDecimal(value.divide(currencyDecimal.value, Constants.mathContext))

    fun toPlainString(): String = value.setScale(2, RoundingMode.HALF_EVEN)
        .stripTrailingZeros()
        .toPlainString()

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

    override fun toString(): String {
        return "CurrencyDecimal(value=$value)"
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

fun String.toCurrencyDecimal(): CurrencyDecimal = CurrencyDecimal.fromString(this)

sealed class CurrenciesEvent {
    object RatesUpdatedError: CurrenciesEvent()
    data class RatesUpdated(val latestRates: LatestRates) : CurrenciesEvent()
    data class BaseCurrencyUpdated(val newBaseCurrency: String) : CurrenciesEvent()
    data class BaseAmountUpdated(val newBaseAmount: CurrencyDecimal?) : CurrenciesEvent()
}

sealed class CurrenciesState {
    object Initializing : CurrenciesState()
    data class Content(
        val amount: CurrencyDecimal?,
        val baseCurrency: Currency,
        val currencies: List<Currency>
    ) : CurrenciesState()
    object Error: CurrenciesState()
}

data class LatestRates(
    val base: String,
    val byCurrencyCode: Map<String, CurrencyDecimal>
)

data class Currency(
    val code: String,
    val rate: CurrencyDecimal
)