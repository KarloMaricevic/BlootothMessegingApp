package com.karlom.bluetoothmessagingapp.feature.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.karlom.bluetoothmessagingapp.feature.chat.components.ChatInputFiled
import com.karlom.bluetoothmessagingapp.feature.chat.components.TextChatBox
import com.karlom.bluetoothmessagingapp.feature.chat.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    address: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .fillMaxWidth()
                .padding(top = 8.dp),
        ) {
            state.messages.forEach { message ->
                TextChatBox(
                    message = message,
                    modifier = Modifier
                        .align(if (message.isFromMe) Alignment.End else Alignment.Start)
                        .padding(bottom = 2.dp),
                )
            }
        }
        ChatInputFiled(
            text = state.textToSend,
            onInteraction = viewModel::onEvent,
        )
    }
}
