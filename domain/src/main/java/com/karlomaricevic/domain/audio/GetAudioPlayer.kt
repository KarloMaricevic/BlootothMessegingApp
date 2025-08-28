package com.karlomaricevic.domain.audio


class GetAudioPlayer constructor(
    private val audioPlayer: AudioPlayer
) {

    operator fun invoke() = audioPlayer
}
