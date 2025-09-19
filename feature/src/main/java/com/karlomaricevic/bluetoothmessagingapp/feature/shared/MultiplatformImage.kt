package com.karlomaricevic.bluetoothmessagingapp.feature.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource

@Composable
fun <T> MultiplatformImage(
    imageIdentifier: T,
    imageResolver: ImageResolver<T>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit
) {
    val res = imageResolver.getImage(imageIdentifier)
    when (res) {
        is ImageResource.Android -> {
            Image(
                painter = painterResource(id = res.resId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale,
            )
        }
        ImageResource.Mock ->  Box(modifier)
    }
}