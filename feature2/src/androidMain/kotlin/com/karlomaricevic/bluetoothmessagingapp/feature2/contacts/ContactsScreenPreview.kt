package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.karlomaricevic.bluetoothmessagingapp.designsystem.BluetoothMessagingAppTheme
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactUi
import com.karlomaricevic.bluetoothmessagingapp.domain.contacts.models.Contact as DomainContact

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
