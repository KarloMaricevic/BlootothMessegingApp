package com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers

import android.content.Context
import com.karlomaricevic.bluetoothmessagingapp.feature.R
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.CHAT_SCREEN_CONNECTING_LABEL
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.CHAT_SCREEN_CONNECT_TO_DEVICE_BUTTON
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.CHAT_SCREEN_MESSAGE_HINT
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.CHAT_SCREEN_RECORDING_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.CHAT_SCREEN_STOPPED_RECORDING_INDICATOR
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.DEFAULT_ANIMATION_LABEL
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.DEFAULT_ICON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.resolvers.models.ChatScreenStringKeys.MICROPHONE_BUTTON_CONTENT_DESCRIPTION
import com.karlomaricevic.bluetoothmessagingapp.feature.shared.resolvers.StringResolver

class ChatStringResolver(private val context: Context): StringResolver<ChatScreenStringKeys> {
    override fun getString(identifier: ChatScreenStringKeys) = when (identifier) {
        DEFAULT_ICON_CONTENT_DESCRIPTION ->  context.getString(R.string.default_icon_content_description)
        CHAT_SCREEN_MESSAGE_HINT -> context.getString(R.string.chat_screen_message_hint)
        CHAT_SCREEN_RECORDING_INDICATOR -> context.getString(R.string.chat_screen_recording_indicator)
        CHAT_SCREEN_STOPPED_RECORDING_INDICATOR -> context.getString(R.string.chat_screen_stopped_recording_indicator)
        CHAT_SCREEN_CONNECTING_LABEL -> context.getString(R.string.chat_screen_connecting_label)
        CHAT_SCREEN_CONNECT_TO_DEVICE_BUTTON -> context.getString(R.string.chat_screen_connect_to_device_button)
        DEFAULT_ANIMATION_LABEL -> context.getString(R.string.default_animation_label)
        MICROPHONE_BUTTON_CONTENT_DESCRIPTION -> context.getString(R.string.chat_screen_microphone_button_content_description)
    }
}
