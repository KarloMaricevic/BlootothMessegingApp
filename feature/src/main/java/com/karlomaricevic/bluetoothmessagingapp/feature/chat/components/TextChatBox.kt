package com.karlomaricevic.bluetoothmessagingapp.feature.chat.components

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
import com.karlomaricevic.domain.messaging.models.SendMessageStatus.NOT_SENT
import com.karlomaricevic.domain.messaging.models.SendMessageStatus.SENDING
import com.karlomaricevic.domain.messaging.models.SendMessageStatus.SENT
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Text

@Composable
fun TextChatBox(
    message: Text,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .padding(
                start = if (message.isFromMe) 0.dp else 4.dp,
                end = if (message.isFromMe) 4.dp else 0.dp,
            )
            .fillMaxWidth(),
    ) {
        Text(
            text = message.message,
            modifier = Modifier
                .align(if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart)
                .then(if (message.state == SENDING || message.state == NOT_SENT) {
                    Modifier.graphicsLayer {
                        alpha = 0.5f
                        shadowElevation = 0f
                        clip = true
                    }
                } else {
                    Modifier
                })
                .then(
                    if (message.state == NOT_SENT) {
                        Modifier.border(1.dp, Color.Red, RoundedCornerShape(16.dp))
                    } else {
                        Modifier
                    }
                )
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 4.dp, horizontal = 8.dp)
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
            state = SENDING,
            timestamp = 0,
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
            state = SENT,
            timestamp = 0,
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
            state = NOT_SENT,
            timestamp = 0,
        )
    )
}
