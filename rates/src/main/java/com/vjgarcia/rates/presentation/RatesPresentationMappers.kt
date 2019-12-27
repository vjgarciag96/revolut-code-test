package com.vjgarcia.rates.presentation

import com.vjgarcia.currencyflags.CurrencyFlags
import com.vjgarcia.rates.domain.LatestRates

fun LatestRates.toRatesState(): RatesState =
    RatesState(
        byCurrencyCode.map { entry ->
            RateRow(
                id = entry.key,
                currencyCode = entry.key,
                currencyValue = entry.value,
                currencyFlagResId = CurrencyFlags.byISOCode[entry.key]
            )
        }
    )