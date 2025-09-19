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
fun <T> MultiplatformIcon(
    imageKey: T,
    imageResolver: ImageResolver<T>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
) {
    when (val res = imageResolver.getImage(imageKey)) {
        is ImageResource.Android -> {
            Image(
                painter = painterResource(res.resId),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = contentScale
            )
        }
        is ImageResource.Mock -> Box(modifier)
    }
}
