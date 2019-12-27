package com.vjgarcia.rates.data

import com.squareup.moshi.Json

data class LatestRatesDTO(
    @Json(name = "base") val base: String,
    @Json(name = "date") val date: String,
    @Json(name = "rates") val rates: Map<String, Double>
)

object LatestRatesError