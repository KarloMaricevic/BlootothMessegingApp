package com.karlom.bluetoothmessagingapp.domain.audio

import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import com.karlom.bluetoothmessagingapp.data.shared.interanlStorage.InternalStorage
import com.karlom.bluetoothmessagingapp.data.audio.VoiceRecorder
import javax.inject.Inject

class StartRecordingVoice @Inject constructor(
    private val voiceRecorder: VoiceRecorder,
    private val internalStorage: InternalStorage,
) {

    operator fun invoke(fileName: String): Either<Failure.ErrorMessage, String> =
        when (val fileUri = internalStorage.createEmptyFile(fileName)) {
            is Either.Left -> fileUri
            is Either.Right -> voiceRecorder.startRecording(fileUri.value)
        }
}
