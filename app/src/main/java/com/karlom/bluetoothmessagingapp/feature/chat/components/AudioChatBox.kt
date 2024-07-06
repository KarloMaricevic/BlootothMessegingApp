package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme
import com.karlom.bluetoothmessagingapp.designSystem.theme.black
import com.karlom.bluetoothmessagingapp.domain.chat.models.MessageState
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent

@Composable
fun AudioChatBox(
    message: Audio,
    onInteraction: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = if (message.isFromMe) 0.dp else 8.dp,
                    end = if (message.isFromMe) 8.dp else 0.dp,
                )
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
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
                            1.dp, Color.Red, RoundedCornerShape(16.dp)
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(start = 12.dp, end = 20.dp)
                .padding(vertical = 10.dp)
        ) {
            Icon(
                painter = painterResource(if (message.isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            onInteraction(
                                if (message.isPlaying) {
                                    ChatScreenEvent.OnPausePlayingAudioMessage
                                } else {
                                    ChatScreenEvent.OnPlayAudioMessage(message)
                                }
                            )
                        },
                    )
                    .padding(end = 10.dp)
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Box(modifier = Modifier
                .padding(end = 12.dp)
                .size(
                    height = 48.dp,
                    width = 80.dp + 44.dp
                )
                .drawBehind {
                    val maxHeight = this.size.height
                    val lineHeights = arrayListOf(
                        maxHeight / 2f,
                        maxHeight / 5f,
                        maxHeight / 3f,
                        maxHeight / 3f,
                        maxHeight / 1.2f,
                        maxHeight / 2f,
                        maxHeight / 3.5f,
                        maxHeight / 1.5f,
                        maxHeight / 1.2f,
                        maxHeight / 2,
                        maxHeight / 2f,
                        maxHeight / 5f,
                        maxHeight / 3f,
                        maxHeight / 3f,
                        maxHeight / 1.2f,
                        maxHeight / 2f,
                        maxHeight / 3.5f,
                        maxHeight / 1.5f,
                        maxHeight / 1.2f,
                        maxHeight / 2,
                    )
                    val lineWidth = 4.dp.toPx()
                    val linePadding = 2.dp.toPx()
                    lineHeights.forEachIndexed { index, item ->
                        drawRoundRect(
                            color = black,
                            topLeft = Offset(
                                y = (maxHeight / 2f) - item / 2,
                                x = index * lineWidth + linePadding + linePadding * index,
                            ),
                            size = Size(lineWidth, item),
                            cornerRadius = CornerRadius(8f, 8f),
                        )
                    }
                }
            )
            Text(text = message.totalTime)
        }
    }
}

@Composable
@Preview
fun AudioChatSendingBoxPreview() {
    BluetoothMessagingAppTheme {
        AudioChatBox(
            message = Audio(
                id = 1,
                filePath = "",
                isFromMe = false,
                totalTime = "1:32",
                state = MessageState.SENDING,
                timestamp = 0,
            ),
            onInteraction = {},
        )
    }
}

@Composable
@Preview
fun AudioChatSentBoxPreview() {
    BluetoothMessagingAppTheme {
        AudioChatBox(
            message = Audio(
                id = 1,
                filePath = "",
                isFromMe = false,
                totalTime = "1:32",
                state = MessageState.SENT,
                timestamp = 0,
            ),
            onInteraction = {},
        )
    }
}

@Composable
@Preview
fun AudioChatNotSentBoxPreview() {
    BluetoothMessagingAppTheme {
        AudioChatBox(
            message = Audio(
                id = 1,
                filePath = "",
                isFromMe = true,
                totalTime = "1:32",
                state = MessageState.NOT_SENT,
                timestamp = 0,
            ),
            onInteraction = {},
        )
    }
}
