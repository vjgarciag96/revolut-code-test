package com.vjgarcia.rates.presentation

import com.vjgarcia.currencyflags.CurrencyFlags
import com.vjgarcia.rates.domain.LatestRates
import java.util.*

fun LatestRates.toRatesState(): RatesState =
    RatesState(
        byCurrencyCode.map { entry ->
            RateRow(
                id = entry.key,
                title = entry.key,
                subtitle = Currency.getInstance(entry.key).displayName,
                currencyFlagResId = CurrencyFlags.byISOCode[entry.key],
                currencyValue = entry.value
            )
        }
    )