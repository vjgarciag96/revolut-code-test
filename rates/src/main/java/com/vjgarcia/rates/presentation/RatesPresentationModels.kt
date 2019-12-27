package com.vjgarcia.rates.presentation

data class RatesState(
    val rates: List<RateRow>
)

data class RateRow(
    val id: String,
    val currencyCode: String,
    val currencyValue: Double
)