package com.karlom.bluetoothmessagingapp.feature.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ChatScreen(address: String) {
    Box {
        Text(
            text = "Chat screen \n chatting with $address",
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
