package com.karlomaricevic.bluetoothmessagingapp.domain.messaging

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.GetMessages
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.SendAudio
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.SendImage
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.SendText
import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.usecase.StartSavingReceivedMessages
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val messagingDomainModule = DI.Module("MessagingDomainModule") {
    bind<StartSavingReceivedMessages>() with provider { StartSavingReceivedMessages(instance()) }
    bind<GetMessages>() with provider { GetMessages(instance()) }
    bind<SendText>() with provider { SendText(instance()) }
    bind<SendAudio>() with provider { SendAudio(instance()) }
    bind<SendImage>() with provider { SendImage(instance()) }
}
