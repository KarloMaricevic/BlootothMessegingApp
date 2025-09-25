package com.karlomaricevic.bluetoothmessagingapp.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import kotlinx.coroutines.CoroutineScope

class AndroidChatViewModel(vmFactory: (CoroutineScope) -> ChatViewModel) : ViewModel() {

    private val sharedVM: ChatViewModel = vmFactory(viewModelScope)

    val state = sharedVM.state

    fun onEvent(event: ChatScreenEvent) {
        sharedVM.onEvent(event)
    }

    override fun onCleared() {
        sharedVM.onCleared()
        super.onCleared()
    }
}
