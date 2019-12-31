package com.vjgarcia.rates.domain

import com.vjgarcia.rates.data.repository.LatestRatesData

fun LatestRatesData.toDomainModel(): LatestRates = LatestRates(
    base = base,
    byCurrencyCode = byCurrencyCode.mapValues { entry ->
        CurrencyDecimal.fromDouble(entry.value)
    }
)