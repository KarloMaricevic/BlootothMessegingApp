package com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.designsystem.blue
import com.karlomaricevic.bluetoothmessagingapp.designsystem.white
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.ContactsImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.ContactsStringsResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys.NO_CONTACTS_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.ADD_CONTACTS_BUTTON
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.DEFAULT_ICON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.NO_CONTACTS_MESSAGE
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.MultiplatformIcon
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

@Composable
fun NoContactsIndicator(
    onInteraction: (ContactScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
    stringResolver: StringResolver<ContactsStringKeys>,
    imageResolver: ImageResolver<ContactsImageKeys>,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MultiplatformIcon(
            imageKey = NO_CONTACTS_ICON,
            imageResolver = imageResolver,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp),
            contentDescription = stringResolver.getString(DEFAULT_ICON_CONTENT_DESCRIPTION),
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResolver.getString(NO_CONTACTS_MESSAGE),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Button(
            onClick = { onInteraction(ContactScreenEvent.OnAddContactClicked) },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = blue,
                contentColor = white,
            ),
        ) {
            Text(stringResolver.getString(ADD_CONTACTS_BUTTON))
        }
    }
}

@Preview
@Composable
private fun NoContactsIndicatorPreview() {
    BluetoothMessagingAppTheme {
        NoContactsIndicator(
            stringResolver = ContactsStringsResolver(LocalContext.current),
            imageResolver = ContactsImageResolver(),
            onInteraction = {},
        )
    }
}
