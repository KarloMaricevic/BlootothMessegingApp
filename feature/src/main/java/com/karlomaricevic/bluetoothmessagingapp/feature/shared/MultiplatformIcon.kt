package com.karlomaricevic.bluetoothmessagingapp.feature.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource.Android
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource.Mock

@Composable
fun <T> MultiplatformIcon(
    imageKey: T,
    imageResolver: ImageResolver<T>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
) {
    when (val res = imageResolver.getImage(imageKey)) {
        is Android -> {
            Icon(
                painter = painterResource(res.resId),
                contentDescription = contentDescription,
                modifier = modifier,
                tint = tint,
            )
        }
        is Mock -> Box(modifier)
    }
}
