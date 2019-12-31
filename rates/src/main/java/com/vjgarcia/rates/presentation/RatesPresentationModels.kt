package com.vjgarcia.rates.presentation

import androidx.annotation.DrawableRes
import com.vjgarcia.rates.domain.CurrencyDecimal
import java.math.BigDecimal

sealed class CurrenciesViewState {
    object Loading: CurrenciesViewState()
    data class Content(val currencies: List<CurrencyRow>): CurrenciesViewState()
}

sealed class CurrenciesEffect {
    object ScrollToTop: CurrenciesEffect()
}

data class CurrencyRow(
    val id: String,
    @DrawableRes val currencyFlagResId: Int?,
    val title: String,
    val subtitle: String,
    val relativeAmount: CurrencyDecimal,
    val editingEnabled: Boolean
)