package com.karlom.bluetoothmessagingapp.domain.audio

import com.karlom.bluetoothmessagingapp.data.audio.AudioPlayer
import javax.inject.Inject

class PauseAudio @Inject constructor(
    private val audioPlayer: AudioPlayer,
) {

    operator fun invoke() = audioPlayer.pause()
}
