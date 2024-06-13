package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.domain.chat.models.MessageState
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Image

@Composable
fun ImageChatBox(
    message: Image,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Box(modifier = modifier.fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(message.imageUri)
                .crossfade(true)
                .build(),
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
                .height(200.dp)
                .aspectRatio(message.aspectRatio),
        )
    }
}

@Preview
@Composable
private fun TextChatBoxSendingPreview() {
    ImageChatBox(
        Image(
            id = 0,
            imageUri = "",
            isFromMe = false,
            aspectRatio = 16 / 9f,
            state = MessageState.SENDING,
        )
    )
}

@Preview
@Composable
private fun TextChatBoxSentPreview() {
    ImageChatBox(
        Image(
            id = 0,
            imageUri = "",
            isFromMe = false,
            aspectRatio = 16 / 9f,
            state = MessageState.SENT,
        )
    )
}

@Preview
@Composable
private fun TextChatBoxNotSentPreview() {
    ImageChatBox(
        Image(
            id = 0,
            imageUri = "",
            isFromMe = false,
            aspectRatio = 16 / 9f,
            state = MessageState.NOT_SENT,
        )
    )
}
