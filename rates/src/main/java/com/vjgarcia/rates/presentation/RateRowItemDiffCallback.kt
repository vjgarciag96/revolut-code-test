package com.vjgarcia.rates.presentation

import androidx.recyclerview.widget.DiffUtil

object RateRowItemDiffCallback : DiffUtil.ItemCallback<RateRow>() {
    override fun areItemsTheSame(oldItem: RateRow, newItem: RateRow): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: RateRow, newItem: RateRow): Boolean =
        oldItem == newItem
}