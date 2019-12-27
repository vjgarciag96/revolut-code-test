package com.vjgarcia.rates.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vjgarcia.rates.R
import com.vjgarcia.rates.ServiceLocator

class RatesFragment : Fragment() {

    private val ratesViewModelFactory = ServiceLocator.ratesViewModelFactory
    private lateinit var ratesViewModel: RatesViewModel

    private val ratesAdapter = RatesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ratesViewModel = ratesViewModelFactory.get(this)
        return inflater.inflate(R.layout.fragment_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRatesList(view)
        bindViewModel()
        ratesViewModel.onRatesVisible()
    }

    override fun onDestroyView() {
        ratesViewModel.onRatesGone()
        super.onDestroyView()
    }

    private fun setUpRatesList(rootView: View) {
        rootView.findViewById<RecyclerView>(R.id.rates).run {
            layoutManager = LinearLayoutManager(context)
            adapter = ratesAdapter
            itemAnimator = null
        }
    }

    private fun bindViewModel() {
        ratesViewModel.ratesState.observe(::getLifecycle) { ratesState ->
            ratesAdapter.submitList(ratesState.rates)
        }
    }

    companion object {
        const val FRAGMENT_TAG = "fragment:rates"
    }
}