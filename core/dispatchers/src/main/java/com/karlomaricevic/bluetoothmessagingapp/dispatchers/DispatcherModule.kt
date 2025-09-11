package com.karlomaricevic.bluetoothmessagingapp.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

const val DefaultDispatcherTag = "DefaultDispatcher"
const val IoDispatcherTag = "IoDispatcher"

val dispatcherModule = DI.Module("Dispatchers") {

    bind<CoroutineDispatcher>(tag = DefaultDispatcherTag) with singleton { Dispatchers.Default }

    bind<CoroutineDispatcher>(tag = IoDispatcherTag) with singleton { Dispatchers.IO }
}
