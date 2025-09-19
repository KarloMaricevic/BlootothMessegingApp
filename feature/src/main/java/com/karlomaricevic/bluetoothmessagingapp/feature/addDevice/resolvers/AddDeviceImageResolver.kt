package com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers

import android.R.attr.name
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenImageKeys.BACK_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.addDevice.resolvers.models.AddDeviceScreenImageKeys.BLUETOOTH_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource

class AddDeviceImageResolver : ImageResolver<AddDeviceScreenImageKeys> {

    private val imageMap = mapOf(
        BACK_ICON to R.drawable.ic_back,
        BLUETOOTH_ICON to R.drawable.ic_bluetooth,
    )

    override fun getImage(identifier: AddDeviceScreenImageKeys): ImageResource {
        val resId = imageMap[identifier] ?: error("Drawable not found: $name")
        return ImageResource.Android(resId)
    }
}
