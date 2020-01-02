package com.vjgarcia.currencies.presentation.model

import androidx.annotation.DrawableRes
import com.vjgarcia.currencies.domain.CurrencyDecimal

sealed class CurrenciesViewState {
    object Loading: CurrenciesViewState()
    data class Content(val currencies: List<CurrencyRow>): CurrenciesViewState()
    object Error: CurrenciesViewState()
}

sealed class CurrenciesEffect {
    object ScrollToTop: CurrenciesEffect()
}

data class CurrencyRow(
    val id: String,
    @DrawableRes val currencyFlagResId: Int?,
    val title: String,
    val subtitle: String,
    val relativeAmount: CurrencyDecimal?,
    val editingEnabled: Boolean
)