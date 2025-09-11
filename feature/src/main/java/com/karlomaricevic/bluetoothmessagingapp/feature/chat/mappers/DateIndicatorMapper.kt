package com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers

import com.karlomaricevic.bluetoothmessagingapp.domain.messaging.models.Message
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.DateIndicator
import java.text.SimpleDateFormat

class DateIndicatorMapper {

    private companion object {
        const val PATTERN = "EEE 'AT' h:mma"
    }

    private val simpleDateFormatter = SimpleDateFormat(PATTERN)

    fun map(message: Message) = DateIndicator(
        simpleDateFormatter.format(message.timestamp).uppercase()
    )
}
