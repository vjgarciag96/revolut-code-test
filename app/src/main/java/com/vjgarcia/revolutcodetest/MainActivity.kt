package com.vjgarcia.revolutcodetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vjgarcia.currencies.presentation.CurrenciesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragmentHost, CurrenciesFragment(), CurrenciesFragment.FRAGMENT_TAG)
            commit()
        }
    }
}
