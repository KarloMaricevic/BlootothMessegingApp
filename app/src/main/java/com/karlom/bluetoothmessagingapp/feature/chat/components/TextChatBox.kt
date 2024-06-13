package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.domain.chat.models.MessageState
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Text

@Composable
fun TextChatBox(
    message: Text,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        Text(
            text = message.message,
            modifier = Modifier
                .align(if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart)
                .then(if (message.state == MessageState.SENDING || message.state == MessageState.NOT_SENT) {
                    Modifier.graphicsLayer {
                        alpha = 0.5f
                        shadowElevation = 0f
                        clip = true
                    }
                } else {
                    Modifier
                })
                .then(
                    if (message.state == MessageState.NOT_SENT) {
                        Modifier.border(
                            1.dp, Color.Red, RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (message.isFromMe) 16.dp else 0.dp,
                                bottomEnd = if (message.isFromMe) 0.dp else 16.dp
                            )
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromMe) 16.dp else 0.dp,
                        bottomEnd = if (message.isFromMe) 0.dp else 16.dp
                    )
                )
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        )
    }
}

@Preview
@Composable
private fun TextChatBoxSendingPreview() {
    TextChatBox(
        Text(
            id = 0,
            message = "Hello!",
            isFromMe = false,
            state = MessageState.SENDING,
        )
    )
}

@Preview
@Composable
private fun TextChatBoxSentPreview() {
    TextChatBox(
        Text(
            id = 0,
            message = "Hello!",
            isFromMe = false,
            state = MessageState.SENT,
        )
    )
}

@Preview
@Composable
private fun TextChatBoxNotSentPreview() {
    TextChatBox(
        Text(
            id = 0,
            message = "Hello!",
            isFromMe = false,
            state = MessageState.NOT_SENT,
        )
    )
}
