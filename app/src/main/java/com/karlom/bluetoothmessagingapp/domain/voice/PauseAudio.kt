package com.karlom.bluetoothmessagingapp.domain.voice

import com.karlom.bluetoothmessagingapp.data.voice.AudioPlayer
import javax.inject.Inject

class PauseAudio @Inject constructor(
    private val audioPlayer: AudioPlayer,
) {

    operator fun invoke() = audioPlayer.pause()
}
