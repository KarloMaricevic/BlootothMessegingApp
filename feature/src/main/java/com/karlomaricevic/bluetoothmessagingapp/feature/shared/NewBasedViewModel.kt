package com.karlomaricevic.bluetoothmessagingapp.feature.shared

import kotlinx.coroutines.CoroutineScope

abstract class NewBaseViewModel<E>(vmScope: CoroutineScope) : CoroutineScope  {

    override val coroutineContext = vmScope.coroutineContext

    abstract fun onEvent(event: E)
}
