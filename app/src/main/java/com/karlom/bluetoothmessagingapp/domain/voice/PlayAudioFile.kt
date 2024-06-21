package com.karlom.bluetoothmessagingapp.domain.voice

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.voice.AudioPlayer
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
