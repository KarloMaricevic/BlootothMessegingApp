package com.karlomaricevic.bluetoothmessagingapp.feature.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components.Contact
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components.NewChatFloatingButton
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components.NoContactsIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactUi
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.ContactsImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.ContactsStringsResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.resolvers.models.ContactsStringKeys.CHAT_TITLE
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.SimplifiedSimpleLazyColumn
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact as DomainContact

@Composable
fun ContactsScreen(
    contacts: List<ContactUi>?,
    stringResolver: StringResolver<ContactsStringKeys> = ContactsStringsResolver(LocalContext.current),
    imageResolver: ImageResolver<ContactsImageKeys> = ContactsImageResolver(),
    onEvent: (ContactScreenEvent) -> Unit,
) {
    Box {
        Column {
            Text(
                text = stringResolver.getString(CHAT_TITLE),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            )
            if (contacts != null) {
                SimplifiedSimpleLazyColumn(
                    items = contacts,
                    key = { contactUi -> contactUi.contact.address },
                    uiItemBuilder = { contactUi ->
                        Contact(
                            model = contactUi,
                            modifier = Modifier
                                .clickable {
                                    onEvent(
                                        OnContactClicked(
                                            contactName = contactUi.contact.name,
                                            address = contactUi.contact.address,
                                        )
                                    )
                                }
                        )
                    },
                    noItemsItem = { NoContactsIndicator(
                        imageResolver = imageResolver,
                        stringResolver = stringResolver,
                        onInteraction =  onEvent,
                    ) },
                    modifier = Modifier.weight(weight = 1f, fill = true),
                )
            }
        }
        NewChatFloatingButton(
            onClick = { onEvent(OnAddContactClicked) },
            stringResolver = stringResolver,
            imageResolver = imageResolver,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreviewEmpty() {
    BluetoothMessagingAppTheme {
        ContactsScreen(
            contacts = emptyList(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsScreenPreviewWithContacts() {
    val contacts = listOf(
        ContactUi(
            contact = DomainContact(name = "Alice", address = "01:23"),
            color = Color.Red,
            lastMessage = "Hey there!",
        ),
        ContactUi(
            contact = DomainContact(name = "Bob", address = "45:67"),
            color = Color.Blue,
            lastMessage = "What's up?",
        ),
        ContactUi(
            contact = DomainContact(name = "Charlie", address = "89:AB"),
            color = Color.Green,
            lastMessage = "Good morning!",
        ),
    )

    BluetoothMessagingAppTheme {
        ContactsScreen(
            contacts = contacts,
            onEvent = {}
        )
    }
}
