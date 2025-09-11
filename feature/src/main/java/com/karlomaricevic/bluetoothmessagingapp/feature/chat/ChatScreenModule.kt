package com.karlomaricevic.bluetoothmessagingapp.feature.chat

import androidx.lifecycle.SavedStateHandle
import com.karlomaricevic.bluetoothmessagingapp.dispatchers.IoDispatcherTag
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.ChatMessageMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.DateIndicatorMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers.SeparatorMapper
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.factory
import org.kodein.di.instance
import org.kodein.di.provider

val chatScreenModule = DI.Module("ChatScreenModule") {
    bind<ChatMessageMapper>() with provider { ChatMessageMapper(instance()) }
    bind<DateIndicatorMapper>() with provider { DateIndicatorMapper() }
    bind<SeparatorMapper>() with provider { SeparatorMapper() }
    bind<ChatViewModel>() with factory { savedStateHandle: SavedStateHandle ->
        ChatViewModel(
            savedStateHandle = savedStateHandle,
            getMessages = instance(),
            sendText = instance(),
            isConnectedTo = instance(),
            sendImage = instance(),
            getAudioPlayer = instance(),
            getConnectionStateNotifier = instance(),
            getVoiceRecorder = instance(),
            deleteAudio = instance(),
            sendAudio = instance(),
            connectToKnownContact = instance(),
            chatMessageMapper = instance(),
            dateIndicatorMapper = instance(),
            separatorMapper = instance(),
            ioDispatcher = instance(tag = IoDispatcherTag),
            navigator = instance()
        )
    }
}
