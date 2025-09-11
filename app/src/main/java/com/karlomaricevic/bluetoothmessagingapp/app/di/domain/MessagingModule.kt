package com.karlomaricevic.bluetoothmessagingapp.app.di.domain

import com.karlomaricevic.domain.messaging.MessageGateway
import com.karlomaricevic.domain.messaging.usecase.GetMessages
import com.karlomaricevic.domain.messaging.usecase.SendAudio
import com.karlomaricevic.domain.messaging.usecase.SendImage
import com.karlomaricevic.domain.messaging.usecase.SendText
import com.karlomaricevic.domain.messaging.usecase.StartSavingReceivedMessages
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object MessagingModule {

    @Provides
    fun providesStartSavingReceivedMessages(gateway: MessageGateway) =
        StartSavingReceivedMessages(gateway)

    @Provides
    fun providesGetMessages(gateway: MessageGateway) = GetMessages(gateway)


    @Provides
    fun providesSendText(gateway: MessageGateway) = SendText(gateway)

    @Provides
    fun providesSendAudio(gateway: MessageGateway) = SendAudio(gateway)

    @Provides
    fun providesSendImage(gateway: MessageGateway) = SendImage(gateway)
}