package com.karlom.bluetoothmessagingapp.feature.chat.mappers

import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.DateIndicator
import com.karlomaricevic.domain.messaging.models.Message
import java.text.SimpleDateFormat
import javax.inject.Inject

class DateIndicatorMapper @Inject constructor() {

    private companion object {
        const val PATTERN = "EEE 'AT' h:mma"
    }

    private val simpleDateFormatter = SimpleDateFormat(PATTERN)

    fun map(message: Message) = DateIndicator(
        simpleDateFormatter.format(message.timestamp).uppercase()
    )
}
