package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme
import com.karlom.bluetoothmessagingapp.designSystem.theme.black
import com.karlom.bluetoothmessagingapp.designSystem.theme.blue
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray500
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged

@Composable
fun ChatInputFiled(
    text: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    onGalleryClicked: () -> Unit,
    mode: ChatInputMode,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
        when (mode) {
            ChatInputMode.TEXT -> {
                ModeSelector(onGalleryClicked)
                ChatTextFiled(
                    text = text,
                    onInteraction = onInteraction,
                    modifier = Modifier.weight(1f, true),
                )
            }

            ChatInputMode.VOICE -> {
                ChatVoiceField()
            }
        }
        Icon(
            painter = painterResource(R.drawable.ic_send),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .clickable { onInteraction(OnSendClicked) }
                .padding(4.dp),
            tint = blue,
        )
    }
}

@Composable
private fun ChatVoiceField(
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_trash),
            contentDescription = stringResource(id = R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .clickable {}
                .size(30.dp)
                .padding(6.dp),
            tint = blue,
        )
        Box(
            modifier
                .padding(top = 4.dp)
                .clip(CircleShape)
                .weight(1f, true)
                .height(30.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(2.dp)
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(0f, size.height / 2 - size.height / 12),
                        size = Size(10f, size.height / 6)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(15f, size.height / 2 - size.height / 6),
                        size = Size(10f, size.height / 3)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(30f, size.height / 6),
                        size = Size(10f, size.height - size.height / 3)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(45f, size.height / 3 - size.height / 4),
                        size = Size(10f, size.height - size.height / 6)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(60f, 0f),
                        size = Size(10f, size.height)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(75f, size.height / 3 - size.height / 4),
                        size = Size(10f, size.height - size.height / 6)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(90f, size.height / 6),
                        size = Size(10f, size.height - size.height / 3)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(105f, size.height / 2 - size.height / 6),
                        size = Size(10f, size.height / 3)
                    )
                    drawRoundRect(
                        color = Color(0xFF0366D6),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        topLeft = Offset(120f, size.height / 2 - size.height / 12),
                        size = Size(10f, size.height / 6)
                    )
                }

        )
    }
}

@Preview
@Composable
private fun ChatVoiceFieldPreview() {
    ChatVoiceField()
}

@Composable
private fun ModeSelector(
    onGalleryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .clickable {}
                .padding(6.dp),
            tint = blue,
        )
        Icon(
            painter = painterResource(R.drawable.ic_gallery),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .clickable { onGalleryClicked() }
                .padding(4.dp),
            tint = blue,
        )
        Icon(
            painter = painterResource(R.drawable.ic_microphone),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .clickable { }
                .padding(4.dp),
            tint = blue,
        )
    }
}

@Composable
private fun ChatTextFiled(
    text: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = text,
        onValueChange = { newText -> onInteraction(OnTextChanged(newText)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        cursorBrush = SolidColor(black),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        modifier = modifier.padding(8.dp),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = stringResource(R.string.chat_screen_message_hint),
                    color = gray500,
                )
            }
            innerTextField()

        }
    )
}

@Preview
@Composable
fun ChatInputFiledPreview() {
    BluetoothMessagingAppTheme {
        ChatInputFiled(
            text = "Message",
            onInteraction = {},
            onGalleryClicked = {},
            mode = ChatInputMode.TEXT
        )
    }
}
