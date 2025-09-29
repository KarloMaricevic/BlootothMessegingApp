package com.karlomaricevic.bluetoothmessagingapp.feature2.utils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

abstract class BaseViewModel<E>() : ViewModel(), CoroutineScope {

    protected companion object {
        const val TIMEOUT_DELAY = 5_000L
    }

    override val coroutineContext
        get() = viewModelScope.coroutineContext

    abstract fun onEvent(event: E)
}
