package com.karlom.bluetoothmessagingapp.feature.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlom.bluetoothmessagingapp.feature.chat.components.ConnectToButton
import com.karlom.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnConnectClicked
import com.karlom.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ChatScreen(address: String) {
    val viewModel = hiltViewModel<ChatViewModel, ChatViewModel.ChatViewModelFactory> { factory ->
        factory.create((address))
    }
    val state by viewModel.state.collectAsState()
    val messages = state.messages.collectAsLazyPagingItems()
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.weight(weight = 1f, fill = true)) {
            if (state.showConnectToDeviceButton) {
                ConnectToButton(
                    onClick = { viewModel.onEvent(OnConnectClicked) },
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
            SimpleLazyColumn(
                items = messages,
                key = { id },
                uiItemBuilder = { message -> TextChatBox(message) },
                noItemsItem = { },
                modifier = Modifier.fillMaxSize()
            )
        }
        ChatInputFiled(
            text = state.textToSend,
            onInteraction = viewModel::onEvent,
        )
    }
}
