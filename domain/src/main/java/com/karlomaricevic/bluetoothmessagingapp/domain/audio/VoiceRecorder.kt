package com.karlomaricevic.bluetoothmessagingapp.domain.audio

import arrow.core.Either
import com.karlomaricevic.bluetoothmessagingapp.core.common.Failure.ErrorMessage

interface VoiceRecorder {
    val isRecording: Boolean
    fun startRecording(): Either<ErrorMessage, String>
    fun stopRecording(): Either<ErrorMessage, Unit>
    fun release()
}
