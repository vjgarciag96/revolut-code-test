package com.vjgarcia.rates.presentation

import androidx.recyclerview.widget.DiffUtil

object RateRowItemDiffCallback : DiffUtil.ItemCallback<CurrencyRow>() {
    override fun areItemsTheSame(oldItem: CurrencyRow, newItem: CurrencyRow): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CurrencyRow, newItem: CurrencyRow): Boolean =
        oldItem == newItem
}