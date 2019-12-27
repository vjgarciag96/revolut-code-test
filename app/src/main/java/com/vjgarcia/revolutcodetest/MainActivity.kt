package com.vjgarcia.revolutcodetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vjgarcia.rates.RatesFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().run {
            add(R.id.fragmentHost, RatesFragment(), RatesFragment.FRAGMENT_TAG)
            commit()
        }
    }
}
