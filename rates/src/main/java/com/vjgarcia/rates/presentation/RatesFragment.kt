package com.vjgarcia.rates.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vjgarcia.rates.R
import com.vjgarcia.rates.ServiceLocator

class RatesFragment : Fragment() {

    private val ratesViewModelFactory = ServiceLocator.ratesViewModelFactory
    private lateinit var ratesViewModel: RatesViewModel

    private lateinit var skeletonView: ViewGroup
    private lateinit var currenciesView: RecyclerView
    private lateinit var ratesAdapter: RatesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ratesViewModel = ratesViewModelFactory.get(this)
        return inflater.inflate(R.layout.fragment_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScreen(view)
        bindViewModel()
        ratesViewModel.onRatesVisible()
    }

    override fun onDestroyView() {
        ratesViewModel.onRatesGone()
        super.onDestroyView()
    }

    private fun setUpScreen(rootView: View) {
        skeletonView = rootView.findViewById(R.id.skeleton)
        ratesAdapter = RatesAdapter(ratesViewModel)
        rootView.findViewById<TextView>(R.id.ratesTitle).text = getString(R.string.ratesScreenTitle)
        currenciesView = rootView.findViewById<RecyclerView>(R.id.rates).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratesAdapter
            itemAnimator = null
        }
    }

    private fun bindViewModel() {
        ratesViewModel.currenciesState.observe(::getLifecycle, ::onCurrenciesState)
        ratesViewModel.currenciesEffect.observe(::getLifecycle, ::onCurrenciesEffect)
    }

    private fun onCurrenciesState(currenciesState: CurrenciesViewState) {
        when (currenciesState) {
            CurrenciesViewState.Loading -> {
                skeletonView.visibility = View.VISIBLE
                currenciesView.visibility = View.GONE
            }
            is CurrenciesViewState.Content -> {
                skeletonView.visibility = View.GONE
                currenciesView.visibility = View.VISIBLE
                ratesAdapter.submitList(currenciesState.currencies)
            }
        }
    }

    private fun onCurrenciesEffect(currenciesEffect: CurrenciesEffect) {
        when (currenciesEffect) {

            CurrenciesEffect.ScrollToTop -> currenciesView.doOnNextLayout {
                currenciesView.smoothScrollToPosition(0)
            }
        }
    }

    companion object {
        const val FRAGMENT_TAG = "fragment:currencies"
    }
}