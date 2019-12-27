package com.vjgarcia.rates.domain

data class LatestRates(
    val byCurrencyCode: Map<String, Double>
)

object LatestRatesError