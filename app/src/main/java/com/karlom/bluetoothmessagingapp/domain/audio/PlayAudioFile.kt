package com.karlom.bluetoothmessagingapp.domain.audio

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.audio.AudioPlayer
import javax.inject.Inject

class PlayAudioFile @Inject constructor(
    private val audioPlayer: AudioPlayer
) {

    suspend operator fun invoke(filePath: String): Either<ErrorMessage, Unit> {
        val initialized = audioPlayer.setDataSource(filePath)
        initialized.onRight { audioPlayer.play() }
        return initialized
    }
}
