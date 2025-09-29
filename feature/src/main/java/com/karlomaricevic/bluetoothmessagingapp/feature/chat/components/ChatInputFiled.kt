package com.karlomaricevic.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.designsystem.black
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.gray500
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatInputMode
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatInputMode.TEXT
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnDeleteVoiceRecordingClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStopRecordingVoiceClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatStringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.SEND_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.DEFAULT_ICON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.MICROPHONE_BUTTON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.MultiplatformIcon
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

@Composable
fun ChatInputFiled(
    text: String,
    inputMode: ChatInputMode,
    isRecording: Boolean,
    onInteraction: (ChatScreenEvent) -> Unit,
    onGalleryClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    stringResolver: StringResolver<ChatScreenStringKeys>,
    imageResolver: ImageResolver<ChatScreenImageKeys>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
    ) {
        when (inputMode) {
            TEXT -> TextInputBox(
                text = text,
                onInteraction = onInteraction,
                onGalleryClicked = onGalleryClicked,
                onMicrophoneClicked = onMicrophoneClicked,
                modifier = Modifier.weight(1f, true),
            )

            VOICE -> VoiceInputBox(
                isRecording = isRecording,
                onInteraction = onInteraction,
                stringResolver = stringResolver,
                modifier = Modifier.weight(1f, true),
            )
        }
        MultiplatformIcon(
            imageKey = SEND_ICON,
            imageResolver = imageResolver,
            contentDescription = stringResolver.getString(DEFAULT_ICON_CONTENT_DESCRIPTION),
            modifier = Modifier
                .padding(bottom = 1.dp)
                .clip(CircleShape)
                .clickable { onInteraction(OnSendClicked) }
                .padding(6.dp),
            tint = blue,
        )
    }
}

@Composable
private fun TextInputBox(
    text: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    onGalleryClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onInteraction(OnSendClicked) }
                .padding(2.dp),
            tint = blue,
        )
        Icon(
            painter = painterResource(R.drawable.ic_gallery),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onGalleryClicked() }
                .padding(2.dp),
            tint = blue,
        )
        Icon(
            painter = painterResource(R.drawable.ic_microphone),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onMicrophoneClicked() }
                .padding(2.dp),
            tint = blue,
        )
        BasicTextField(
            text, { newText -> onInteraction(OnTextChanged(newText)) }, Modifier
                .weight(1f, true)
                .padding(bottom = 6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 2.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            cursorBrush = SolidColor(black),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onBackground,
                platformStyle = PlatformTextStyle(includeFontPadding = false),
            ),
            decorationBox = { innerTextField ->
                Row(Modifier.padding(start = 8.dp, end = 16.dp)) {
                    if (text.isEmpty()) {
                        Text(
                            text = stringResource(R.string.chat_screen_message_hint),
                            color = gray500,
                        )
                    }
                    innerTextField()
                }
            })
    }
}

@Composable
private fun VoiceInputBox(
    isRecording: Boolean,
    onInteraction: (ChatScreenEvent) -> Unit,
    stringResolver: StringResolver<ChatScreenStringKeys>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = stringResolver.getString(MICROPHONE_BUTTON_CONTENT_DESCRIPTION),
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { onInteraction(OnDeleteVoiceRecordingClicked) }
                .padding(2.dp),
            tint = blue,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .weight(1f, true)
                .padding(vertical = 2.dp),
        ) {
            Box(Modifier.height(24.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_pause),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .then(if (isRecording) {
                        Modifier.clickable { onInteraction(OnStopRecordingVoiceClicked) }
                    } else {
                        Modifier
                    }),
                tint = if (isRecording) blue else gray500,
            )
            Text(
                text = if (isRecording) stringResource(R.string.chat_screen_recording_indicator) else stringResource(
                    R.string.chat_screen_stopped_recording_indicator
                ),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                ),
            )
        }
    }
}

@Composable
private fun AnimatedVoice() {
    var time by remember { mutableIntStateOf(10) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
    ) {
        val maxHeight = this.size.height
        val maxWidth = this.size.width
        val heightsInterval = arrayListOf(
            maxHeight / 5f,
            maxHeight / 2f,
            maxHeight,
            maxHeight / 2f,
        )
        val lineWidth = 4.dp.toPx()
        val linePadding = 2.dp.toPx()
        val intervalWidth = heightsInterval.size * lineWidth + linePadding * heightsInterval.size

        var drawnWidth = 0f
        var period = 0
        var numberOfLinesDrawn = 0
        if (drawnWidth < maxWidth) {
            while (drawnWidth < maxWidth || numberOfLinesDrawn > time) {
                for (index in 0..heightsInterval.lastIndex) {
                    drawnWidth =
                        intervalWidth * period + index * lineWidth + linePadding + linePadding * index + lineWidth + linePadding / 2f
                    if (drawnWidth > maxWidth || numberOfLinesDrawn >= time) {
                        break
                    }
                    drawRoundRect(
                        color = white,
                        topLeft = Offset(
                            y = (maxHeight / 2f) - heightsInterval[index] / 2,
                            x = intervalWidth * period + index * lineWidth + linePadding + linePadding * index,
                        ),
                        size = Size(lineWidth, heightsInterval[index]),
                        cornerRadius = CornerRadius(8f, 8f),
                    )
                    numberOfLinesDrawn += 1
                }
                period += 1
            }
        } else {
            drawLine(black, start = Offset(x = 0f, y = maxHeight / 2), end = Offset(x = maxWidth, y = maxHeight / 2), 1.dp.toPx())
        }
    }
}

@Preview
@Composable
private fun AnimatedVoicePreview() {
    AnimatedVoice()
}

@Preview
@Composable
fun ChatTextInputPreview() {
    BluetoothMessagingAppTheme {
        ChatInputFiled(
            text = "Message",
            onInteraction = {},
            onGalleryClicked = {},
            inputMode = TEXT,
            isRecording = false,
            onMicrophoneClicked = {},
            stringResolver = ChatStringResolver(LocalContext.current),
            imageResolver = ChatImageResolver(),
        )
    }
}

@Preview
@Composable
private fun ChatVoiceInputPreview() {
    BluetoothMessagingAppTheme {
        ChatInputFiled(
            text = "",
            onInteraction = {},
            onGalleryClicked = {},
            inputMode = VOICE,
            isRecording = false,
            onMicrophoneClicked = {},
            stringResolver = ChatStringResolver(LocalContext.current),
            imageResolver = ChatImageResolver(),
        )
    }
}
