package com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karlomaricevic.bluetoothmessagingapp.feature2.contacts.models.ContactUi

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
        CircleWithLetter(
            color = model.color,
            letter = model.contact.name.firstOrNull()?.uppercase() ?: "?")
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
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

@Composable
private fun CircleWithLetter(
    color: Color,
    letter: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(48.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = color,
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
