package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.core.di.IoDispatcher
import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import com.karlom.bluetoothmessagingapp.domain.bluetooth.usecase.IsConnectedToDevice
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.GetMessages
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendImage
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnConnectClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendImageClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatViewModel.ChatViewModelFactory::class)
class ChatViewModel @AssistedInject constructor(
    @Assisted private val contactAddress: String,
    private val getMessages: GetMessages,
    private val sendMessage: SendMessage,
    private val mesRepository: ChatRepository,
    private val isConnectedToDevice: IsConnectedToDevice,
    private val sendImage: SendImage,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<ChatScreenEvent>() {

    private val showConnectToDeviceButton = MutableStateFlow(!isConnectedToDevice(contactAddress))
    private val textToSend = MutableStateFlow("")
    private val messages = MutableStateFlow(getMessages(contactAddress))

    val state = combine(
        showConnectToDeviceButton,
        textToSend,
        messages,
        ::ChatScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = ChatScreenState(
            showConnectToDeviceButton = !isConnectedToDevice(
                contactAddress
            )
        ),
    )

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is OnTextChanged -> textToSend.update { event.text }
            is OnSendClicked -> viewModelScope.launch { sendMessage(textToSend.value) }
            is OnConnectClicked -> {}
            is OnSendImageClicked -> viewModelScope.launch(ioDispatcher) { sendImage(event.uri) }
        }
    }

    @AssistedFactory
    interface ChatViewModelFactory {

        fun create(contactAddress: String): ChatViewModel
    }
}
