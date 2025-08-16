package com.karlom.bluetoothmessagingapp.domain.audio

import com.karlom.bluetoothmessagingapp.data.shared.interanlStorage.InternalStorage
import javax.inject.Inject

class DeleteAudioFile @Inject constructor(
    private val storage: InternalStorage
) {

    operator fun invoke(fileName: String) = storage.deleteFile(fileName)
}
