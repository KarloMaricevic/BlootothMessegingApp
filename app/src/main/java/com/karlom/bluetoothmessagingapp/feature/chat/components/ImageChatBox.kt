package com.karlom.bluetoothmessagingapp.feature.chat.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.karlom.bluetoothmessagingapp.R
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message

@Composable
fun ImageChatBox(
    message: Message.ImageMessage,
    modifier: Modifier = Modifier,
) {
    var aspectRatio by remember { mutableStateOf(1f) }
    val context = LocalContext.current
    val imageLoader = ImageLoader(context)

    LaunchedEffect(message.imageUri) {
        val request = ImageRequest.Builder(context)
            .data(message.imageUri)
            .size(coil.size.Size.ORIGINAL)
            .build()

        val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
        result?.let {
            aspectRatio = it.intrinsicWidth.toFloat() / it.intrinsicHeight.toFloat()
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(message.imageUri)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.default_icon_content_description),
            modifier = Modifier
                .align(if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromMe) 16.dp else 0.dp,
                        bottomEnd = if (message.isFromMe) 0.dp else 16.dp
                    )
                )
                .height(200.dp)
                .aspectRatio(aspectRatio),
        )
    }
}

@Preview
@Composable
private fun TextChatBoxPreview() {
    ImageChatBox(
        Message.ImageMessage(
            id = 0,
            imageUri = "",
            isFromMe = false,
        )
    )
}
