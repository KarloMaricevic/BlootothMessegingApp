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
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Audio
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
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromMe) 16.dp else 0.dp,
                        bottomEnd = if (message.isFromMe) 0.dp else 16.dp
                    )
                )
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
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onInteraction(ChatScreenEvent.OnPlayAudioMessage(message)) },
                    )
                    .padding(
                        start = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                    )
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Box(modifier = Modifier
                .padding(vertical = 4.dp)
                .padding(end = 8.dp)
                .size(
                    height = 52.dp,
                    width = 40.dp + 22.dp
                )
                .drawBehind {
                    val maxHeight = this.size.height
                    val lineHeights = arrayListOf(
                        maxHeight / 2f,
                        maxHeight / 5f,
                        maxHeight / 3f,
                        maxHeight / 3f,
                        maxHeight,
                        maxHeight / 2f,
                        maxHeight / 3.5f,
                        maxHeight / 1.5f,
                        maxHeight,
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
            Text(
                text = message.totalTime,
                modifier = Modifier.padding(end = 4.dp),
            )
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
                audioUri = "",
                isFromMe = false,
                totalTime = "1:32",
                state = MessageState.SENDING,
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
                audioUri = "",
                isFromMe = false,
                totalTime = "1:32",
                state = MessageState.SENT,
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
                audioUri = "",
                isFromMe = false,
                totalTime = "1:32",
                state = MessageState.NOT_SENT,
            ),
            onInteraction = {},
        )
    }
}
