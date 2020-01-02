package com.vjgarcia.currencies.data.network

import com.squareup.moshi.Json

data class LatestRatesDTO(
    @Json(name = "base") val base: String,
    @Json(name = "date") val date: String,
    @Json(name = "rates") val rates: Map<String, Double>
)

object LatestRatesApiError