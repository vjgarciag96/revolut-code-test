package com.vjgarcia.rates.domain

import com.vjgarcia.rates.data.repository.LatestRatesData

fun LatestRatesData.toDomain(): LatestRates = LatestRates(byCurrencyCode)