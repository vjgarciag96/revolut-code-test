package com.vjgarcia.currencies.data.repository

data class LatestRatesData(
    val base: String,
    val byCurrencyCode: Map<String, Double>
)

object LatestRatesDataError