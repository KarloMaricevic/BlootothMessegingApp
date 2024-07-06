package com.karlom.bluetoothmessagingapp.feature.chat.mappers

import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.ChatMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.DateIndicator
import java.text.SimpleDateFormat
import javax.inject.Inject

class DateIndicatorMapper @Inject constructor() {

    private companion object {
        const val PATTERN = "EEE 'AT' h:mma"
    }

    private val simpleDateFormatter = SimpleDateFormat(PATTERN)

    fun map(message: ChatMessage) = DateIndicator(
        simpleDateFormatter.format(message.timestamp).uppercase()
    )
}
