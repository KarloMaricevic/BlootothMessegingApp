package com.karlomaricevic.bluetoothmessagingapp.feature.chat.mappers

import com.karlomaricevic.domain.messaging.models.Message
import com.karlomaricevic.bluetoothmessagingapp.feature.chat.models.ChatItem.MessageSeparator
import kotlin.math.absoluteValue

class SeparatorMapper {

    private companion object {
        const val SMALL_SEPARATION = 2
        const val BIG_SEPARATION = 8
        const val ONE_MINUTE_IN_MILLIS = 60000
    }

    fun map(before: Message?, after: Message?) =
        if (before is Message.TextMessage && after is Message.TextMessage) {
            if (isWithinOneMinute(before.timestamp, after.timestamp)) {
                MessageSeparator(
                    id = "${MessageSeparator::class.java.name} ${before.id}",
                    value = SMALL_SEPARATION,
                )
            } else {
                MessageSeparator(
                    id = "${MessageSeparator::class.java.name} ${before.id}",
                    value = BIG_SEPARATION,
                )
            }
        } else {
            null
        }

    private fun isWithinOneMinute(timestamp1: Long, timestamp2: Long): Boolean {
        val difference = (timestamp1 - timestamp2).absoluteValue
        return difference < ONE_MINUTE_IN_MILLIS
    }
}
