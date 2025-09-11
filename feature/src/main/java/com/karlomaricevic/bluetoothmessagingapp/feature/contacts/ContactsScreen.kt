package com.karlomaricevic.bluetoothmessagingapp.feature.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components.Contact
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components.NewChatFloatingButton
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.components.NoContactsIndicator
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.*
import com.karlomaricevic.bluetoothmessagingapp.feature.contacts.viewmodel.ContactsViewModel
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.SimplifiedSimpleLazyColumn

@Composable
fun ContactsScreen(viewModel: ContactsViewModel) {
    val contactsState = viewModel.contacts.collectAsState()
    val contacts = contactsState.value
    Box {
        Column {
            Text(
                text = stringResource(id = R.string.contacts_screen_chat_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
            )
            if (contacts != null) {
                SimplifiedSimpleLazyColumn(
                    items = contacts,
                    key = { contact -> contact.contact.address },
                    uiItemBuilder = { contactUi ->
                        Contact(
                            model = contactUi,
                            modifier = Modifier
                                .testTag("contact-${contactUi.contact.address}")
                                .clickable {
                                    viewModel.onEvent(
                                        OnContactClicked(
                                            contactName = contactUi.contact.name,
                                            address = contactUi.contact.address,
                                        )
                                    )
                                }
                        )
                    },
                    noItemsItem = { NoContactsIndicator(viewModel::onEvent) },
                    modifier = Modifier.weight(weight = 1f, fill = true),
                )
            }
        }
        NewChatFloatingButton(
            onClick = { viewModel.onEvent(OnAddContactClicked) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        )
    }
}
