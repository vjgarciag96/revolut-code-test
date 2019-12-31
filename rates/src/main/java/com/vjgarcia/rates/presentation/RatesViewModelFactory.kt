package com.vjgarcia.rates.presentation

import androidx.fragment.app.Fragment
import com.vjgarcia.commons.buildViewModel
import com.vjgarcia.rates.domain.GetCurrencies
import com.vjgarcia.rates.domain.UpdateBaseCurrency
import com.vjgarcia.rates.domain.UpdateCurrentAmount

internal class RatesViewModelFactory(
    private val getCurrencies: GetCurrencies,
    private val updateBaseCurrency: UpdateBaseCurrency,
    private val updateCurrentAmount: UpdateCurrentAmount
) {

    fun get(fragment: Fragment): RatesViewModel = fragment.buildViewModel {
        RatesViewModel(
            getCurrencies,
            updateBaseCurrency,
            updateCurrentAmount
        )
    }
}