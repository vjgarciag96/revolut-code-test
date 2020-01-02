package com.vjgarcia.currencies.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.vjgarcia.currencies.presentation.model.CurrencyRow

object CurrencyRowItemDiffCallback : DiffUtil.ItemCallback<CurrencyRow>() {
    override fun areItemsTheSame(oldItem: CurrencyRow, newItem: CurrencyRow): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CurrencyRow, newItem: CurrencyRow): Boolean =
        oldItem == newItem
}