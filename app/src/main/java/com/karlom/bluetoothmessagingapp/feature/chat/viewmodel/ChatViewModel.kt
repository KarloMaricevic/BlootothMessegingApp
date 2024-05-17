package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.data.chat.ChatRepository
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.GetMessages
import com.karlom.bluetoothmessagingapp.domain.chat.usecase.SendMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.*
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : BaseViewModel<ChatScreenEvent>() {

    private val textToSend = MutableStateFlow("")
    private val messages = MutableStateFlow(getMessages(contactAddress))

    val state = combine(
        textToSend,
        messages,
        ::ChatScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = ChatScreenState(),
    )

    init {
        mesRepository.getMessageReceiver().onRight { messageFlow ->
            viewModelScope.launch {
                messageFlow.collect {}
            }
        }
    }

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is OnTextChanged -> textToSend.update { event.text }
            is OnSendClicked -> viewModelScope.launch { sendMessage(textToSend.value) }
            is OnConnectClicked -> {}
        }
    }

    @AssistedFactory
    interface ChatViewModelFactory {

        fun create(contactAddress: String): ChatViewModel
    }
}
