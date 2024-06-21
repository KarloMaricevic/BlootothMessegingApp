package com.karlom.bluetoothmessagingapp.domain.voice

import com.karlom.bluetoothmessagingapp.data.voice.AudioPlayer
import javax.inject.Inject

class StopPlayingAudio @Inject constructor(
    private val audioPlayer: AudioPlayer
) {

    operator fun invoke() {
        audioPlayer.releaseMediaPlayer()
    }
}
