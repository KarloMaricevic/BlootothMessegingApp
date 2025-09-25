package com.karlomaricevic.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatScreenEvent.OnBackClicked
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.ChatStringResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.AVATAR_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.BACK_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.DEFAULT_ICON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.MultiplatformIcon
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

@Composable
fun ChatScreenToolbar(
    contactName: String,
    onInteraction: (ChatScreenEvent) -> Unit,
    stringResolver: StringResolver<ChatScreenStringKeys>,
    imageResolver: ImageResolver<ChatScreenImageKeys>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 9.dp,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MultiplatformIcon(
                imageKey = BACK_ICON,
                imageResolver = imageResolver,
                contentDescription = stringResolver.getString(DEFAULT_ICON_CONTENT_DESCRIPTION),
                modifier = Modifier
                    .padding(end = 8.dp, start = 12.dp)
                    .clip(CircleShape)
                    .clickable { onInteraction(OnBackClicked) }
                    .padding(12.dp)
                    .size(16.dp)
            )
            MultiplatformIcon(
                imageKey = AVATAR_ICON,
                imageResolver = imageResolver,
                contentDescription = stringResolver.getString(DEFAULT_ICON_CONTENT_DESCRIPTION),
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
        stringResolver = ChatStringResolver(LocalContext.current),
        imageResolver = ChatImageResolver(),
    )
}

@Preview
@Composable
fun ChatScreenLongTextToolbarPreview() {
    ChatScreenToolbar(
        contactName = List(10) { "ContactName" }.joinToString(""),
        onInteraction = {},
        stringResolver = ChatStringResolver(LocalContext.current),
        imageResolver = ChatImageResolver(),
    )
}
