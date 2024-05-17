package com.karlom.bluetoothmessagingapp.feature.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlom.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ChatScreen(address: String) {
    val viewModel =
        hiltViewModel<ChatViewModel, ChatViewModel.ChatViewModelFactory> { factory ->
            factory.create((address))
        }
    val state by viewModel.state.collectAsState()
    val messages = state.messages.collectAsLazyPagingItems()
    Column(Modifier.fillMaxSize()) {
        SimpleLazyColumn(
            items = messages,
            key = { id },
            uiItemBuilder = { message -> TextChatBox(message) },
            noItemsItem = { },
            modifier = Modifier.weight(weight = 1f, fill = true)
        )
        ChatInputFiled(
            text = state.textToSend,
            onInteraction = viewModel::onEvent,
        )
    }
}
