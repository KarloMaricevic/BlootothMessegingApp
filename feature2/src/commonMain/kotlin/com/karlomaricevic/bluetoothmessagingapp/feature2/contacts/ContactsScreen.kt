package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.components.Contact
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.components.NewChatFloatingButton
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.components.NoContactsIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactScreenEvent.OnAddContactClicked
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactScreenEvent.OnContactClicked
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactUi
import com.karlomaricevic.bluetoothmessagingapp.feature2.utils.components.SimplifiedSimpleLazyColumn
import com.karlomaricevic.bluetoothmessagingapp.feature2.Res
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts_screen_chat_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContactsScreen(
    contacts: List<ContactUi>?,
    onEvent: (ContactScreenEvent) -> Unit,
) {
    Box {
        Column {
            Text(
                text = stringResource(Res.string.contacts_screen_chat_title),
                style = MaterialTheme.typography.h5,
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
                    noItemsItem = { NoContactsIndicator(onEvent) },
                    modifier = Modifier.weight(weight = 1f, fill = true),
                )
            }
        }
        NewChatFloatingButton(
            onClick = { onEvent(OnAddContactClicked) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
        )
    }
}
