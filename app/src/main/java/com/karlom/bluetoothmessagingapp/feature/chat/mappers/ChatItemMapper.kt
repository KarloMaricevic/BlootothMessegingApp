package com.karlom.bluetoothmessagingapp.feature.chat.mappers

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message.AudioMessage
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message.ImageMessage
import com.karlom.bluetoothmessagingapp.domain.chat.models.Message.TextMessage
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Audio
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Image
import com.karlom.bluetoothmessagingapp.feature.chat.models.ChatItem.Text
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ChatItemMapper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private companion object {
        const val ERROR_READING_ASPECT_RATIO = 0f
        const val ERROR_READING_AUDIO_TOTAL_TIME = 0L
    }

    fun map(message: Message) =
        when (message) {
            is TextMessage -> Text(
                id = message.id,
                message = message.message,
                isFromMe = message.isFromMe,
                state = message.state,
            )

            is ImageMessage -> Image(
                id = message.id,
                isFromMe = message.isFromMe,
                imageUri = message.imageUri,
                aspectRatio = getImageAspectRatio(message.imageUri),
                state = message.state,
            )

            is AudioMessage -> Audio(
                id = message.id,
                filePath = message.audioUri,
                isFromMe = message.isFromMe,
                totalTime = formatToTimeString(getAudioDuration(message.audioUri)),
                state = message.state,
            )
        }

    private fun getImageAspectRatio(filePath: String): Float {
        return try {
            val fileStream =
                context.contentResolver.openInputStream(File(filePath).toUri())
                    ?: return ERROR_READING_ASPECT_RATIO
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(fileStream, null, options)
            fileStream.close()
            val width = options.outWidth
            val height = options.outHeight
            if (height != 0) {
                width.toFloat() / height
            } else {
                ERROR_READING_ASPECT_RATIO
            }
        } catch (e: Exception) {
            ERROR_READING_ASPECT_RATIO
        }
    }


    private fun getAudioDuration(path: String) = try {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(path)
        val duration =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        duration?.toLong() ?: ERROR_READING_AUDIO_TOTAL_TIME
    } catch (e: IllegalArgumentException) {
        ERROR_READING_AUDIO_TOTAL_TIME
    }

    private fun formatToTimeString(millis: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        var time = ""
        if (hours > 0) {
            time += "$hours:"
        }
        time += "$minutes:$seconds"
        return time
    }
}
