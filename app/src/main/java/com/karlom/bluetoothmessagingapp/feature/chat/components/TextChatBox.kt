package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message.TextMessage

@Composable
fun TextChatBox(
    message: TextMessage,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        Text(
            text = message.message,
            modifier = Modifier
                .align(if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart)
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
private fun TextChatBoxPreview() {
    TextChatBox(
        TextMessage(
            id = 0,
            message = "Hello!",
            isFromMe = false,
        )
    )
}
