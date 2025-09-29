package com.karlomaricevic.bluetoothmessagingapp.app

import android.app.Application
import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.app.di.appModule
import com.karlomaricevic.bluetoothmessagingapp.app.navigation.navigationModule
import com.karlomaricevic.bluetoothmessagingapp.bluetooth.bluetoothModule
import com.karlomaricevic.bluetoothmessagingapp.data.audio.audioDataModule
import com.karlomaricevic.bluetoothmessagingapp.data.connection.connectionDataModule
import com.karlomaricevic.bluetoothmessagingapp.data.contact.contactsDataModule
import com.karlomaricevic.bluetoothmessagingapp.data.db.dbModule
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.messagingDataModule
import com.karlomaricevic.bluetoothmessagingapp.dispatchers.dispatcherModule
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.audioDomainModule
import com.karlomaricevic.bluetoothmessagingapp.domain.connection.connectionDomainModule
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.contactsDomainModule
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.messagingDomainModule
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.addDeviceScreenModule
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.chatScreenModule
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.contactsScreenModule
import com.karlomaricevic.bluetoothmessagingapp.platform.platformModule
import org.kodein.di.DI

class BluetoothMessagingApplication : Application() {
    lateinit var di: DI
        private set


    override fun onCreate() {
        super.onCreate()
        val androidContext: Context = this
        di = DI {
            import(appModule(androidContext))
            import(dbModule)
            import(navigationModule)
            import(platformModule)
            import(dispatcherModule)
            import(bluetoothModule)
            import(audioDataModule)
            import(contactsDataModule)
            import(connectionDataModule)
            import(messagingDataModule)
            import(audioDomainModule)
            import(contactsDomainModule)
            import(connectionDomainModule)
            import(messagingDomainModule)
            import(addDeviceScreenModule)
            import(contactsScreenModule)
            import(chatScreenModule)
        }
    }
}
