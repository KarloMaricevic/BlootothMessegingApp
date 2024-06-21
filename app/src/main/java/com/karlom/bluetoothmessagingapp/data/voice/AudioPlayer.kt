package com.karlom.bluetoothmessagingapp.data.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var mediaPlayer: MediaPlayer? = null


    suspend fun setDataSource(audioUri: String): Either<ErrorMessage, Unit> {
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
                        continuation.resume(Left(ErrorMessage("Error while setting up datasource")))
                        true
                    }
                    prepareAsync()
                    continuation.invokeOnCancellation {
                        setOnPreparedListener(null)
                        setOnErrorListener(null)
                        reset()
                    }
                }
            } catch (e: IllegalStateException) {
                continuation.resume(Left(ErrorMessage("Cant set datasource in this player state")))
            }
        }
    }


    fun play(): Either<ErrorMessage, Unit> = try {
        mediaPlayer?.start()
        Right(Unit)
    } catch (e: IllegalStateException) {
        Left(ErrorMessage(e.message ?: "Unknown"))
    }

    fun pause() = try {
        mediaPlayer?.pause()
        Right(Unit)
    } catch (e: IllegalStateException) {
        Left(ErrorMessage(e.message ?: "Unknown"))
    }

    fun stop() = try {
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        Right(Unit)
    } catch (e: IllegalStateException) {
        Left(ErrorMessage(e.message ?: "Unknown"))
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}