package com.karlom.bluetoothmessagingapp.domain.audio

import com.karlom.bluetoothmessagingapp.data.shared.interanlStorage.InternalStorage
import javax.inject.Inject

class CreateAudioFile @Inject constructor(
    private val storage: InternalStorage,
) {

    private companion object {
        const val AUDIO_EXTENSION = "3GPP"
    }

    operator fun invoke(fileName: String) = storage.createEmptyFile("$fileName.$AUDIO_EXTENSION")
}
