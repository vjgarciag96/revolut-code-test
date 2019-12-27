package com.vjgarcia.revolutcodetest

import androidx.multidex.MultiDexApplication
import com.vjgarcia.currencyflags.CurrencyFlags

class RatesApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        CurrencyFlags.init()
    }
}