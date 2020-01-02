package com.vjgarcia.currencies.data.repository

import com.vjgarcia.currencies.data.network.LatestRatesDTO

fun LatestRatesDTO.toDataModel(): LatestRatesData =
    LatestRatesData(base = base, byCurrencyCode = rates)