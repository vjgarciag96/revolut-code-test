package com.vjgarcia.commons

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

inline fun <reified T : ViewModel> Fragment.buildViewModel(crossinline factory: () -> T): T =
    ViewModelProviders.of(this, getViewModelFactory(factory)).get(T::class.java)

inline fun <reified T : ViewModel> getViewModelFactory(crossinline factory: () -> T): ViewModelProvider.Factory =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <U : ViewModel> create(modelClass: Class<U>): U = factory() as U
    }