package com.karlom.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.karlom.bluetoothmessagingapp.core.base.BaseViewModel
import com.karlom.bluetoothmessagingapp.core.base.TIMEOUT_DELAY
import com.karlom.bluetoothmessagingapp.domain.chat.models.TextMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
) : BaseViewModel<ChatScreenEvent>() {

    private val textToSend = MutableStateFlow("")
    private val messages = MutableStateFlow(
        listOf(
            TextMessage(1, "Hi!", false),
            TextMessage(2, "Hi!", true),
            TextMessage(3, "How is your day?", false),
        )
    )

    val state = combine(
        textToSend,
        messages,
        ::ChatScreenState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_DELAY),
        initialValue = ChatScreenState(),
    )

    override fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is OnTextChanged -> textToSend.update { event.text }
            is OnSendClicked -> {}
        }
    }
}
