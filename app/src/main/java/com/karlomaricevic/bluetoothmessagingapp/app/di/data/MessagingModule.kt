package com.karlomaricevic.bluetoothmessagingapp.app.di.data

import com.karlomaricevic.bluetoothmessagingapp.bluetooth.communicationMenager.BluetoothCommunicationManager
import com.karlomaricevic.data.db.daos.MessageDao
import com.karlomaricevic.data.messaging.MessageLocalDataSource
import com.karlomaricevic.data.messaging.MessagingRepositoryImpl
import com.karlomaricevic.data.messaging.mappers.MessageMapper
import com.karlomaricevic.domain.messaging.MessageGateway
import com.karlomaricevic.platform.utils.FileStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface MessagingModule {

    companion object {

        @Provides
        fun providesMessageMapper() = MessageMapper()

        @Singleton
        @Provides
        fun provideMessageLocalDataSource(
            dao: MessageDao,
            mapper: MessageMapper,
        ) = MessageLocalDataSource(dao = dao, mapper = mapper)

        @Singleton
        @Provides
        fun providesMessagingRepositoryImpl(
            communicationManager: BluetoothCommunicationManager,
            localDataSource: MessageLocalDataSource,
            internalStorage: FileStorage,
            mapper: MessageMapper,
        ) = MessagingRepositoryImpl(
            communicationManager = communicationManager,
            localMessages = localDataSource,
            internalStorage = internalStorage,
        )
    }

    @Singleton
    @Binds
    fun bindsMessageGateway(repository: MessagingRepositoryImpl): MessageGateway
}
