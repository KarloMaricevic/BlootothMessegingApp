package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.TEXT
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatInputMode.VOICE
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnDeleteVoiceRecordingClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnStopRecordingVoiceClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged

@Composable
fun ChatInputFiled(
    text: String,
    inputMode: ChatInputMode,
    isRecording: Boolean,
    onInteraction: (ChatScreenEvent) -> Unit,
    onGalleryClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp),
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
                modifier = Modifier.weight(1f, true),
            )
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
private fun TextInputBox(
    text: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    onGalleryClicked: () -> Unit,
    onMicrophoneClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_camera),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .clickable { onInteraction(OnSendClicked) }
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
                .clickable { onMicrophoneClicked() }
                .padding(4.dp),
            tint = blue,
        )
        BasicTextField(
            value = text,
            onValueChange = { newText -> onInteraction(OnTextChanged(newText)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            cursorBrush = SolidColor(black),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .padding(8.dp)
                .weight(1f, true),
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
}

@Composable
private fun VoiceInputBox(
    isRecording: Boolean,
    onInteraction: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .padding(end = 4.dp)
                .clickable { onInteraction(OnDeleteVoiceRecordingClicked) },
            tint = blue,
        )
        Row(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .weight(1f, true)
                .padding(vertical = 2.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_pause),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clickable { onInteraction(OnStopRecordingVoiceClicked) },
                tint = if (isRecording) blue else gray500,
            )
            Text(
                text = if (isRecording) stringResource(R.string.chat_screen_recording_indicator) else stringResource(
                    R.string.chat_screen_stopped_recording_indicator
                ),
            )
        }
    }
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
        )
    }
}
