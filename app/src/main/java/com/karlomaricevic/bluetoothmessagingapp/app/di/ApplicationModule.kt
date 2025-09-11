package com.karlomaricevic.bluetoothmessagingapp.app.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import com.karlomaricevic.bluetoothmessagingapp.app.viewmodel.GlobalViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.singleton

fun appModule(androidContext: Context) = DI.Module("AppModule") {
    bind<Context>() with singleton { androidContext.applicationContext }
    bindSingleton {
        GlobalViewModel(
            closeConnection = instance(),
            startSavingReceivedMessages = instance(),
        )
    }
}
