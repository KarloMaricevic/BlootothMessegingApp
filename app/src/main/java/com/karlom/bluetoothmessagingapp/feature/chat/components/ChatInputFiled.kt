package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray300
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray500
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnSendClicked
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnTextChanged

@Composable
fun ChatInputFiled(
    text: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(gray300)
                .weight(1f, true)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newText -> onInteraction(OnTextChanged(newText)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                cursorBrush = SolidColor(black),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(Modifier.padding(vertical = 4.dp)) {
                        if (text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.chat_screen_message_hint),
                                color = gray500,
                            )
                        }
                        innerTextField()
                    }
                }
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

@Preview
@Composable
fun ChatInputFiledPreview() {
    BluetoothMessagingAppTheme {
        ChatInputFiled(
            text = "Message",
            onInteraction = {},
        )
    }
}
