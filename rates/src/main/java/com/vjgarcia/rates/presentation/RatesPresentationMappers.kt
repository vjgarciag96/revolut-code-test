package com.vjgarcia.rates.presentation

import com.vjgarcia.rates.domain.LatestRates

fun LatestRates.toRatesState(): RatesState =
    RatesState(
        byCurrencyCode.map { entry ->
            RateRow(
                id = entry.key,
                currencyCode = entry.key,
                currencyValue = entry.value
            )
        }
    )