package com.karlomaricevic.bluetoothmessagingapp.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.left
import arrow.core.right
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage
import com.karlomaricevic.bluetoothmessagingapp.domain.audio.AudioPlayer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AudioPlayerImpl(private val context: Context): AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null

    override suspend fun setDataSource(audioUri: String): Either<ErrorMessage, Unit> {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            }
        }
        return suspendCancellableCoroutine { continuation ->
            try {
                mediaPlayer?.apply {
                    setDataSource(context, audioUri.toUri())
                    setOnPreparedListener { continuation.resume(Right(Unit)) }
                    setOnErrorListener { _, _, _ ->
                        reset()
                        setOnPreparedListener(null)
                        setOnErrorListener(null)
                        continuation.resume(ErrorMessage("Error while setting up datasource").left())
                        true
                    }
                    prepareAsync()
                    continuation.invokeOnCancellation {
                        setOnPreparedListener(null)
                        setOnErrorListener(null)
                        reset()
                    }
                }
            } catch (_: IllegalStateException) {
                continuation.resume(ErrorMessage("Cant set datasource in this player state").left())
            }
        }
    }


    override fun play() = try {
        mediaPlayer?.start()
        Unit.right()
    } catch (e: IllegalStateException) {
        ErrorMessage(e.message ?: "Unknown").left()
    }

    override fun pause() = try {
        mediaPlayer?.pause()
        Unit.right()
    } catch (e: IllegalStateException) {
        ErrorMessage(e.message ?: "Unknown").left()
    }

    override fun stop() = try {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        Unit.right()
    } catch (e: IllegalStateException) {
        ErrorMessage(e.message ?: "Unknown").left()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
