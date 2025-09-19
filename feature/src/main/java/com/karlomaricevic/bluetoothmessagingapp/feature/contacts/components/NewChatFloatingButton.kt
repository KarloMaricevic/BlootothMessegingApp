package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.ContactsImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.ContactsStringsResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys.CHAT_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.DEFAULT_ICON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.NEW_CHAT
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.MultiplatformIcon
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

@Composable
fun NewChatFloatingButton(
    onClick: () -> Unit,
    stringResolver: StringResolver<ContactsStringKeys>,
    imageResolver: ImageResolver<ContactsImageKeys>,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = blue,
        contentColor = white,
    ) {
        Row(Modifier.padding(16.dp)) {
            MultiplatformIcon(
                imageKey = CHAT_ICON,
                imageResolver = imageResolver,
                contentDescription = stringResolver.getString(DEFAULT_ICON_CONTENT_DESCRIPTION),
                modifier = Modifier.padding(end = 8.dp),
            )
            Text(text = stringResolver.getString(NEW_CHAT))
        }
    }
}

@Preview
@Composable
fun NewChatFloatingButtonPreview() {
    BluetoothMessagingAppTheme {
        NewChatFloatingButton(
            stringResolver = ContactsStringsResolver(LocalContext.current),
            imageResolver = ContactsImageResolver(),
            onClick = {},
        )
    }
}
