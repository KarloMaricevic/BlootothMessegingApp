package com.karlomaricevic.bluetoothmessagingapp.domain.audio

class GetAudioPlayer(private val audioPlayer: AudioPlayer) {

    operator fun invoke() = audioPlayer
}
