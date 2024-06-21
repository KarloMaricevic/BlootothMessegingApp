package com.karlom.bluetoothmessagingapp.domain.voice

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure.ErrorMessage
import com.karlom.bluetoothmessagingapp.data.voice.AudioPlayer
import javax.inject.Inject

class PlayInitializedAudio @Inject constructor(
    private val audioPlayer: AudioPlayer
) {

    operator fun invoke(): Either<ErrorMessage, Unit> = audioPlayer.play()
}
