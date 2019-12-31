package com.vjgarcia.rates.data.repository

import com.vjgarcia.rates.data.network.LatestRatesDTO

fun LatestRatesDTO.toDataModel(): LatestRatesData =
    LatestRatesData(base = base, byCurrencyCode = rates)