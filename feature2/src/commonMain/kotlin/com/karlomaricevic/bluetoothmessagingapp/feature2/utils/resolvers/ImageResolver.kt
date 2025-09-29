package com.karlomaricevic.bluetoothmessagingapp.feature2.utils.resolvers

import com.karlomaricevic.bluetoothmessagingapp.feature2.utils.resolvers.models.ImageResource


interface ImageResolver<T> {
    fun getImage(identifier: T): ImageResource
}
