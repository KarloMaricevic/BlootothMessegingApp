package com.karlomaricevic.bluetoothmessagingapp.domain.audio

class DeleteAudio(private val repository: AudioRepository) {

    operator fun invoke(name: String) = repository.deleteAudio(name)
}
