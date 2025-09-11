package com.karlomaricevic.bluetoothmessagingapp.data.audio

import com.karlomaricevic.bluetoothmessagingapp.domain.audio.AudioRepository
import com.karlomaricevic.bluetoothmessagingapp.platform.FileStorage

class AudioRepositoryImpl(private val storage: FileStorage): AudioRepository {

    override fun deleteAudio(path: String) = storage.deleteFile(path)
}
