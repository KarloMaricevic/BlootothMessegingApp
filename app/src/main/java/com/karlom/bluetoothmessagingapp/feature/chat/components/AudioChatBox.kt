package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme
import com.karlom.bluetoothmessagingapp.domain.chat.models.MessageState
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent

@Composable
fun AudioChatBox(
    message: Audio,
    onInteraction: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = stringResource(R.string.default_icon_content_description),
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
                .clickable { onInteraction(ChatScreenEvent.OnPlayAudioMessage(message)) }
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
                .size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
@Preview
fun AudioChatSendingBoxPreview() {
    BluetoothMessagingAppTheme {
        AudioChatBox(
            message = Audio(id = 1, audioUri = "", isFromMe = false, state = MessageState.SENDING),
            onInteraction = {},
        )
    }
}

@Composable
@Preview
fun AudioChatSentBoxPreview() {
    BluetoothMessagingAppTheme {
        AudioChatBox(
            message = Audio(id = 1, audioUri = "", isFromMe = false, state = MessageState.SENT),
            onInteraction = {},
        )
    }
}

@Composable
@Preview
fun AudioChatNotSentBoxPreview() {
    BluetoothMessagingAppTheme {
        AudioChatBox(
            message = Audio(id = 1, audioUri = "", isFromMe = false, state = MessageState.NOT_SENT),
            onInteraction = {},
        )
    }
}