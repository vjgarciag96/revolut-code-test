package com.vjgarcia.currencies.domain

import com.vjgarcia.currencies.data.repository.LatestRatesData

fun LatestRatesData.toDomainModel(): LatestRates = LatestRates(
    base = base,
    byCurrencyCode = byCurrencyCode.mapValues { entry ->
        CurrencyDecimal.fromDouble(entry.value)
    }
)