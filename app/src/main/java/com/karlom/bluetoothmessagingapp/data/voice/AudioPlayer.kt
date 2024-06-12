package com.karlom.bluetoothmessagingapp.data.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.core.net.toUri
import arrow.core.Either
import arrow.core.Either.*
import com.karlom.bluetoothmessagingapp.core.models.Failure
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


    suspend fun playLocalAudio(audioUri: String): Either<Failure.ErrorMessage, Unit> =
        if (mediaPlayer != null) {
            Left(Failure.ErrorMessage("Player is already in use"))
        } else {
            suspendCancellableCoroutine { continuation ->
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(context, audioUri.toUri())
                    setOnPreparedListener {
                        continuation.resume(Right(Unit))
                        start()
                    }
                    setOnCompletionListener { releaseMediaPlayer() }
                    prepareAsync()
                    continuation.invokeOnCancellation {
                        setOnPreparedListener(null)
                        setOnCompletionListener(null)
                        releaseMediaPlayer()
                    }
                }
            }
        }


    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}