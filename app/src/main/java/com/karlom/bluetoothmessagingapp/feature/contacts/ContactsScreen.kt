package com.karlom.bluetoothmessagingapp.feature.contacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.karlom.bluetoothmessagingapp.feature.contacts.components.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.components.NewChatFloatingButton
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
            uiItemBuilder = { contact -> Contact(model = contact) },
            noItemsItem = { },
            modifier = Modifier.fillMaxSize(),
        )
        NewChatFloatingButton(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        )
    }
}
