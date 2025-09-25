package com.karlomaricevic.bluetoothmessagingapp.feature.chat.components

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatStringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.CHAT_SCREEN_CONNECT_TO_DEVICE_BUTTON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.DEFAULT_ANIMATION_LABEL
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

@Composable
fun ConnectToButton(
    isConnecting: Boolean,
    onClick: (() -> Unit)?,
    stringResolver: StringResolver<ChatScreenStringKeys>,
    modifier: Modifier = Modifier
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
            label = stringResolver.getString(DEFAULT_ANIMATION_LABEL),
        ) { isConnecting ->
            if (isConnecting) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResolver.getString(CHAT_SCREEN_CONNECT_TO_DEVICE_BUTTON),
                        modifier = Modifier.padding(end = 4.dp),
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 3.dp,
                    )
                }
            } else {
                Text(text =stringResolver.getString(CHAT_SCREEN_CONNECT_TO_DEVICE_BUTTON))
            }
        }
    }
}

@Preview
@Composable
fun ConnectToButtonPreview() {
    ConnectToButton(
        isConnecting = true,
        stringResolver = ChatStringResolver(LocalContext.current),
        onClick = {},
    )
}

@Preview
@Composable
fun ConnectToButtonNotConnectingPreview() {
    ConnectToButton(
        isConnecting = false,
        stringResolver = ChatStringResolver(LocalContext.current),
        onClick = {}
    )
}
