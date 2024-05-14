package com.karlom.bluetoothmessagingapp.feature.contacts.components

import androidx.compose.foundation.background

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme
import com.karlom.bluetoothmessagingapp.designSystem.theme.gray300
import com.karlom.bluetoothmessagingapp.domain.contacts.models.Contact
import com.karlom.bluetoothmessagingapp.feature.contacts.models.ContactUi

@Composable
fun Contact(
    model: ContactUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .border(1.dp, gray300, CircleShape)
                .background(color = model.color)
                .padding(20.dp),
        ) {
            Text(text = model.contact.name.firstOrNull()?.toString() ?: "?")
        }
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = model.contact.name,
                maxLines = 1,
            )
            Text(
                text = model.lastMessage,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview
@Composable
fun ContactPreview() {
    BluetoothMessagingAppTheme {
        Contact(
            ContactUi(
                contact = Contact(name = "Contact 1", address = ""),
                lastMessage = "You: Hello this is a fake message",
                color = Color.Blue,
            )
        )
    }
}

@Preview
@Composable
fun ContactLongTextPreview() {
    BluetoothMessagingAppTheme {
        Contact(
            ContactUi(
                contact = Contact(
                    name = List(size = 20, init = { "Contact 1" }).joinToString(""),
                    address = "",
                ),
                lastMessage = List(
                    size = 20,
                    init = { "You: Hello this is a fake message" }).joinToString(""),
                color = Color.Blue,
            )
        )
    }
}
