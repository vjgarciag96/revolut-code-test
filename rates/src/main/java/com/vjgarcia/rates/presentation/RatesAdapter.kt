package com.vjgarcia.rates.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.vjgarcia.rates.R

class RatesAdapter : ListAdapter<RateRow, RateRowViewHolder>(RateRowItemDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateRowViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RateRowViewHolder(layoutInflater.inflate(R.layout.rate_row, parent, false))
    }

    override fun onBindViewHolder(holder: RateRowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}