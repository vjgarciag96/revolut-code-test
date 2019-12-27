package com.vjgarcia.rates.data.repository

data class LatestRatesData(val byCurrencyCode: Map<String, Double>)

object LatestRatesDataError