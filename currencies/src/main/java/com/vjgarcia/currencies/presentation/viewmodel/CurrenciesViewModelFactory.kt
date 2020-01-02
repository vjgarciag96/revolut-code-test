package com.vjgarcia.currencies.presentation.viewmodel

import androidx.fragment.app.Fragment
import com.vjgarcia.commons.buildViewModel
import com.vjgarcia.currencies.domain.GetCurrencies
import com.vjgarcia.currencies.domain.UpdateBaseCurrency
import com.vjgarcia.currencies.domain.UpdateCurrentAmount

class CurrenciesViewModelFactory(
    private val getCurrencies: GetCurrencies,
    private val updateBaseCurrency: UpdateBaseCurrency,
    private val updateCurrentAmount: UpdateCurrentAmount
) {

    fun get(fragment: Fragment): CurrenciesViewModel = fragment.buildViewModel {
        CurrenciesViewModel(
            getCurrencies,
            updateBaseCurrency,
            updateCurrentAmount
        )
    }
}