package com.vjgarcia.currencies.presentation.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vjgarcia.currencies.R
import com.vjgarcia.currencies.presentation.model.CurrenciesEventsListener
import com.vjgarcia.currencies.presentation.model.CurrencyRow

class CurrencyAdapter(
    private val currenciesEventsListener: CurrenciesEventsListener
) : ListAdapter<CurrencyRow, CurrencyRowViewHolder>(CurrencyRowItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyRowViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CurrencyRowViewHolder(layoutInflater.inflate(R.layout.currency_row, parent, false))
    }

    override fun onBindViewHolder(holder: CurrencyRowViewHolder, position: Int) {
        holder.bind(getItem(position), currenciesEventsListener)
    }
}