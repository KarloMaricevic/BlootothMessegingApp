package com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models

sealed class ImageResource {
    data class Android(val resId: Int) : ImageResource()
    object Mock: ImageResource()
}
