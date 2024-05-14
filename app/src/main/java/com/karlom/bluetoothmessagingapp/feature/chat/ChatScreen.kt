package com.karlom.bluetoothmessagingapp.feature.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlom.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ChatScreen(
    address: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val messages = state.messages.collectAsLazyPagingItems()
    Column(Modifier.fillMaxSize()) {
        SimpleLazyColumn(
            items = messages,
            key = { id },
            uiItemBuilder = { message ->
                TextChatBox(
                    message = message,
                    modifier = Modifier
                        .align(if (message.isFromMe) Alignment.End else Alignment.Start)
                        .padding(bottom = 2.dp),
                )
            },
            noItemsItem = { },
            modifier = Modifier.weight(weight = 1f, fill = true)
        )
        ChatInputFiled(
            text = state.textToSend,
            onInteraction = viewModel::onEvent,
        )
    }
}
