package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnBackClicked

@Composable
fun ChatScreenToolbar(
    contactName: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 9.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .padding(end = 8.dp, start = 12.dp)
                    .clip(CircleShape)
                    .clickable { onInteraction(OnBackClicked) }
                    .padding(12.dp)
                    .size(16.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_avatar),
                contentDescription = stringResource(R.string.default_icon_content_description),
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp),
            )
            Text(
                text = contactName,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 24.dp),
                maxLines = 1,
            )
        }
    }
}

@Preview
@Composable
fun ChatScreenToolbarPreview() {
    ChatScreenToolbar(
        contactName = "ContactName",
        onInteraction = {},
    )
}

@Preview
@Composable
fun ChatScreenLongTextToolbarPreview() {
    ChatScreenToolbar(
        contactName = List(10) { "ContactName" }.joinToString(""),
        onInteraction = {},
    )
}
