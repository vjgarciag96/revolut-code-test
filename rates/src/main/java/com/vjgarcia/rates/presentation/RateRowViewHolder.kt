package com.vjgarcia.rates.presentation

import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.google.android.material.textfield.TextInputEditText
import com.vjgarcia.rates.R

internal class RateRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Dirty fix to avoid reusing recycled text watchers with inlined valued from another row
    private var textWatcher: TextWatcher? = null

    fun bind(
        currencyRow: CurrencyRow,
        ratesEventsListener: RatesEventsListener
    ) {
        itemView.findViewById<TextView>(R.id.currencyTitle).text = currencyRow.title
        itemView.findViewById<TextView>(R.id.currencySubtitle).text = currencyRow.subtitle
        itemView.findViewById<ImageView>(R.id.currencyIcon).run {
            Glide.with(itemView.context)
                .load(currencyRow.currencyFlagResId)
                .transform(FitCenter(), CircleCrop())
                .into(this)
        }
        itemView.findViewById<TextInputEditText>(R.id.currencyValueTextField).run {
            textWatcher?.let { removeTextChangedListener(textWatcher) }
            setText(currencyRow.relativeAmount.toPlainString())
            if (currencyRow.editingEnabled) {
                inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
                textWatcher = doOnTextChanged { text, _, _, _ ->
                    ratesEventsListener.onAmountChanged(text ?: "0")
                }
            } else {
                keyListener = null
                inputType = InputType.TYPE_NULL

            }

            itemView.setOnClickListener {
                ratesEventsListener.onRowClicked(currencyRow)
            }
        }
    }
}