package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.blue

@Composable
fun ConnectToButton(
    isConnecting: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Box(modifier
        .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
        .then(
            if (onClick != null) {
                Modifier.clickable { onClick() }
            } else Modifier
        )
        .background(blue)
        .padding(8.dp),
    ) {
        AnimatedContent(
            targetState = isConnecting,
            label = stringResource(R.string.default_animation_label),
        ) { isConnecting ->
            if (isConnecting) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.chat_screen_connecting_label),
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 3.dp,
                    )
                }
            } else {
                Text(text = stringResource(R.string.chat_screen_connect_to_device_button))
            }
        }
    }
}

@Preview
@Composable
fun ConnectToButtonPreview() {
    ConnectToButton(isConnecting = true, onClick = {})
}

@Preview
@Composable
fun ConnectToButtonNotConnectingPreview() {
    ConnectToButton(isConnecting = false, onClick = {})
}
