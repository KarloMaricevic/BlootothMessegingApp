package com.karlomaricevic.data.audio

import com.karlomaricevic.domain.audio.AudioRepository
import com.karlomaricevic.platform.utils.FileStorage

class AudioRepositoryImpl(private val storage: FileStorage): AudioRepository {

    override fun deleteAudio(path: String) = storage.deleteFile(path)
}
