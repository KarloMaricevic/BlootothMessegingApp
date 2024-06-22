package com.karlom.bluetoothmessagingapp.feature.contacts.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karlom.bluetoothmessagingapp.designSystem.theme.BluetoothMessagingAppTheme
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
        CircleWithLetter(model.contact.name.firstOrNull()?.uppercase() ?: "?")
        Column(
            Modifier.padding(
                start = 8.dp,
                end = 16.dp,
            )
        ) {
            Text(
                text = model.contact.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = model.lastMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CircleWithLetter(letter: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(48.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color.Blue,
                radius = size.minDimension / 2
            )
        }
        Text(
            text = letter,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
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
