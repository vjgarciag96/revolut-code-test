package com.vjgarcia.rates.presentation

import androidx.fragment.app.Fragment
import com.vjgarcia.commons.buildViewModel
import com.vjgarcia.rates.domain.GetLatestRates

internal class RatesViewModelFactory(
    private val getLatestRates: GetLatestRates
) {

    fun get(fragment: Fragment): RatesViewModel = fragment.buildViewModel {
        RatesViewModel(getLatestRates)
    }
}