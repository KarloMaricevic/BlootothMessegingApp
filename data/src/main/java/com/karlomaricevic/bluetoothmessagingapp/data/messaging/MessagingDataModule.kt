package com.karlomaricevic.bluetoothmessagingapp.data.messaging

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlomaricevic.bluetoothmessagingapp.data.db.daos.MessageDao
import com.karlomaricevic.bluetoothmessagingapp.data.messaging.mappers.MessageMapper
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.MessageGateway
import com.karlomaricevic.bluetoothmessagingapp.platform.FileStorage
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val messagingDataModule = DI.Module("MessagingDataModule") {
    bind<MessageMapper>() with singleton { MessageMapper() }
    bind<MessageLocalDataSource>() with singleton {
        MessageLocalDataSource(
            dao = instance<MessageDao>(),
            mapper = instance<MessageMapper>(),
        )
    }
    bind<MessageGateway>() with singleton {
        MessagingRepositoryImpl(
            communicationManager = instance<BluetoothCommunicationManager>(),
            localMessages = instance<MessageLocalDataSource>(),
            internalStorage = instance<FileStorage>(),
        )
    }
}