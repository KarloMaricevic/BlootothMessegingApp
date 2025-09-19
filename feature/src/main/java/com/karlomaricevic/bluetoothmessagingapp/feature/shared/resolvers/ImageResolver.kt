package com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers

import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource

interface ImageResolver<T> {
    fun getImage(identifier: T): ImageResource
}
