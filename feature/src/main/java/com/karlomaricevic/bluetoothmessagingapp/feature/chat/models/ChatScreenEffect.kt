package com.karlomaricevic.bluetoothmessagingapp.feature.chat.models

sealed interface ChatScreenEffect {

    data class Error(val errorMessage: String) : ChatScreenEffect

    data object ScrollToBottom: ChatScreenEffect
}
