package com.karlomaricevic.bluetoothmessagingapp.feature2.utils.resolvers.models

sealed class ImageResource {
    data class Android(val resId: Int) : ImageResource()
    object Mock: ImageResource()
}
