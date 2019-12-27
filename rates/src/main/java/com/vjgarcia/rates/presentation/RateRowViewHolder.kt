package com.vjgarcia.rates.presentation

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.vjgarcia.rates.R

class RateRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(rateRow: RateRow) {
        itemView.findViewById<TextView>(R.id.currencyTitle).text = rateRow.title
        itemView.findViewById<TextView>(R.id.currencySubtitle).text = rateRow.subtitle
        itemView.findViewById<ImageView>(R.id.currencyIcon).run {
            Glide.with(itemView.context)
                .load(rateRow.currencyFlagResId)
                .transform(FitCenter(), CircleCrop())
                .into(this)
        }
    }
}