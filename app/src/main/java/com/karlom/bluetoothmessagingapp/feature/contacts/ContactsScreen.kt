package com.karlom.bluetoothmessagingapp.feature.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.karlom.bluetoothmessagingapp.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.karlom.bluetoothmessagingapp.designSystem.theme.blue
import com.karlom.bluetoothmessagingapp.designSystem.theme.white
import com.karlom.bluetoothmessagingapp.feature.contacts.components.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.components.NewChatFloatingButton
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnAddContactClicked
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactScreenEvent.OnContactClicked
import com.karlom.bluetoothmessagingapp.feature.contacts.viewmodel.ContactsViewModel
import com.karlom.bluetoothmessagingapp.feature.shared.SimpleLazyColumn

@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val contacts = viewModel.contacts.collectAsLazyPagingItems()

    Box {
        SimpleLazyColumn(
            items = contacts,
            key = { contact.address },
            titleItemBuilder = {
                Text(text = stringResource(id = R.string.contacts_screen_chat_title))
            },
            uiItemBuilder = { contactUi ->
                Contact(
                    model = contactUi,
                    modifier = Modifier.clickable {
                        viewModel.onEvent(OnContactClicked(contactUi.contact.address))
                    })
            },
            noItemsItem = {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mail_box),
                        contentDescription = stringResource(id = R.string.default_icon_content_description),
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 10.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.contacts_screen_no_contacts),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Button(
                        onClick = { viewModel.onEvent(OnAddContactClicked) },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue, contentColor = white),
                    ) {
                        Text(text = stringResource(id = R.string.contacts_screen_add_contacts))
                    }
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        if (!(contacts.loadState.append.endOfPaginationReached && contacts.itemCount == 0)) {
            NewChatFloatingButton(
                onClick = { viewModel.onEvent(OnAddContactClicked) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
            )
        }
    }
}
