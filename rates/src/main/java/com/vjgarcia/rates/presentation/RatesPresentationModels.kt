package com.vjgarcia.rates.presentation

import androidx.annotation.DrawableRes

data class RatesState(
    val rates: List<RateRow>
)

data class RateRow(
    val id: String,
    @DrawableRes val currencyFlagResId: Int?,
    val currencyCode: String,
    val currencyValue: Double
)