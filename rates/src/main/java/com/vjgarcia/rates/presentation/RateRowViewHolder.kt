package com.vjgarcia.rates.presentation

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vjgarcia.rates.R

class RateRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(rateRow: RateRow) {
        itemView.findViewById<TextView>(R.id.currencyTitle).text = rateRow.currencyCode
        itemView.findViewById<TextView>(R.id.currencySubtitle).text = rateRow.currencyValue.toString()
    }
}