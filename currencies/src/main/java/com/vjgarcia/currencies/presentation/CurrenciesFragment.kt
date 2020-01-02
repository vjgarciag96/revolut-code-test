package com.vjgarcia.currencies.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vjgarcia.currencies.R
import com.vjgarcia.currencies.ServiceLocator
import com.vjgarcia.currencies.presentation.model.CurrenciesEffect
import com.vjgarcia.currencies.presentation.model.CurrenciesViewState
import com.vjgarcia.currencies.presentation.recyclerview.CurrencyAdapter
import com.vjgarcia.currencies.presentation.viewmodel.CurrenciesViewModel

class CurrenciesFragment : Fragment() {

    private val currenciesViewModelFactory = ServiceLocator.currenciesViewModelFactory
    private lateinit var currenciesViewModel: CurrenciesViewModel

    private lateinit var skeletonView: ViewGroup
    private lateinit var currenciesView: RecyclerView
    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var errorView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currenciesViewModel = currenciesViewModelFactory.get(this)
        return inflater.inflate(R.layout.fragment_currencies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpScreen(view)
        bindViewModel()
    }

    override fun onDestroyView() {
        currenciesViewModel.onRatesGone()
        super.onDestroyView()
    }

    private fun setUpScreen(rootView: View) {
        skeletonView = rootView.findViewById(R.id.skeleton)
        errorView = rootView.findViewById(R.id.error)
        currencyAdapter = CurrencyAdapter(currenciesViewModel)
        rootView.findViewById<TextView>(R.id.currenciesTitle).text = getString(R.string.ratesScreenTitle)
        currenciesView = rootView.findViewById<RecyclerView>(R.id.rates).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currencyAdapter
            itemAnimator = null
        }
    }

    private fun bindViewModel() {
        currenciesViewModel.currenciesState.observe(::getLifecycle, ::onCurrenciesState)
        currenciesViewModel.currenciesEffect.observe(::getLifecycle, ::onCurrenciesEffect)
        currenciesViewModel.onRatesVisible()
    }

    private fun onCurrenciesState(currenciesState: CurrenciesViewState) {
        when (currenciesState) {
            CurrenciesViewState.Loading -> {
                skeletonView.visibility = View.VISIBLE
                currenciesView.visibility = View.GONE
                errorView.visibility = View.GONE

            }
            is CurrenciesViewState.Content -> {
                skeletonView.visibility = View.GONE
                errorView.visibility = View.GONE
                currenciesView.visibility = View.VISIBLE
                currencyAdapter.submitList(currenciesState.currencies)
            }
            CurrenciesViewState.Error -> {
                skeletonView.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                currenciesView.visibility = View.GONE
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