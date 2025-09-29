package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.feature2.Res
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts_screen_new_chat
import com.karlomaricevic.bluetoothmessagingapp.feature2.default_icon_content_description
import com.karlomaricevic.bluetoothmessagingapp.feature2.ic_chat
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewChatFloatingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        backgroundColor = blue,
        contentColor = white,
    ) {
        Row(Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(Res.drawable.ic_chat),
                modifier = Modifier.padding(end = 8.dp),
                contentDescription = stringResource(Res.string.default_icon_content_description),
            )
            Text(text = stringResource(Res.string.contacts_screen_new_chat))
        }
    }
}
