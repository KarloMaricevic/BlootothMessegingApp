package com.karlom.bluetoothmessagingapp.feature.contacts.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme

@Composable
fun NewChatFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_chat),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(text = stringResource(R.string.contacts_screen_new_chat))
        }
    }
}

@Preview
@Composable
fun NewChatFloatingButtonPreview() {
    BluetoothMessagingAppTheme {
        NewChatFloatingButton(onClick = {})
    }
}
