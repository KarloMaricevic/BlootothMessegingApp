package com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers

import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.AVATAR_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.BACK_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.CAMERA_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.DELETE_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.GALLERY_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.MICROPHONE_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.PAUSE_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenImageKeys.SEND_ICON
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.ImageResolver
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.models.ImageResource.Android

class ChatImageResolver: ImageResolver<ChatScreenImageKeys> {

    override fun getImage(identifier: ChatScreenImageKeys) = when (identifier) {
        SEND_ICON -> Android(R.drawable.ic_send)
        CAMERA_ICON -> Android(R.drawable.ic_camera)
        GALLERY_ICON -> Android(R.drawable.ic_gallery)
        MICROPHONE_ICON -> Android(R.drawable.ic_microphone)
        DELETE_ICON -> Android(R.drawable.ic_delete)
        PAUSE_ICON -> Android(R.drawable.ic_pause)
        BACK_ICON -> Android(R.drawable.ic_back)
        AVATAR_ICON -> Android(R.drawable.ic_avatar)
        ChatScreenImageKeys.PLAY_ICON -> Android(R.drawable.ic_play)
    }
}
