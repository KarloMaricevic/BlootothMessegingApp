package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.blue

@Composable
fun ConnectToButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.chat_screen_connect_to_device_button),
        modifier = modifier
            .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
            .clickable { onClick() }
            .background(blue)
            .padding(8.dp),
    )
}

@Preview
@Composable
fun ConnectToButtonPreview() {
    ConnectToButton(onClick = {})
}