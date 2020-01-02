package com.vjgarcia.currencies.presentation.recyclerview

import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.google.android.material.textfield.TextInputEditText
import com.vjgarcia.currencies.R
import com.vjgarcia.currencies.domain.toCurrencyDecimal
import com.vjgarcia.currencies.presentation.model.CurrenciesEventsListener
import com.vjgarcia.currencies.presentation.model.CurrencyRow

class CurrencyRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Dirty fix to avoid reusing recycled text watchers with inlined values from another row
    private var textWatcher: TextWatcher? = null

    fun bind(
        currencyRow: CurrencyRow,
        currenciesEventsListener: CurrenciesEventsListener
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

            val currentText = text?.toString()

            val newAmount = currencyRow.relativeAmount
            when {
                newAmount == null -> setText("")
                currentText != null && currentText.isNotBlank() && currentText.isNotEmpty() -> {
                    val currentAmount = currentText.toCurrencyDecimal()

                    if (currentAmount != newAmount) {
                        setText(newAmount.toPlainString())
                    }
                }
                else -> setText(newAmount.toPlainString())
            }

            if (currencyRow.editingEnabled) {
                inputType = InputType.TYPE_CLASS_NUMBER.or(InputType.TYPE_NUMBER_FLAG_DECIMAL)
                textWatcher = doAfterTextChanged { text ->
                    text?.let { currenciesEventsListener.onAmountChanged(it) }
                }
            } else {
                keyListener = null
                inputType = InputType.TYPE_NULL

            }

            itemView.setOnClickListener {
                currenciesEventsListener.onRowClicked(currencyRow)
            }
        }
    }
}