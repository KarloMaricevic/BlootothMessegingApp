package com.karlom.bluetoothmessagingapp.feature.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.feature.contacts.components.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.components.NewChatFloatingButton
import com.karlom.bluetoothmessagingapp.feature.contacts.components.NoContactsIndicator
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnAddContactClicked
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnContactClicked
import com.karlom.bluetoothmessagingapp.feature.contacts.viewmodel.ContactsViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val contacts = viewModel.contacts.collectAsLazyPagingItems()

    Box(Modifier.padding(top = 8.dp)) {
        Column {
            Text(
                text = stringResource(id = R.string.contacts_screen_chat_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 8.dp),
            )
            SimpleLazyColumn(
                items = contacts,
                key = { contact.address },
                uiItemBuilder = { contactUi ->
                    Contact(
                        model = contactUi,
                        modifier = Modifier.clickable {
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
        NewChatFloatingButton(
            onClick = { viewModel.onEvent(OnAddContactClicked) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        )
    }
}
