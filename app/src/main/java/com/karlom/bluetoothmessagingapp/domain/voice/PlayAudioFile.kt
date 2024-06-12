package com.karlom.bluetoothmessagingapp.domain.voice

import com.karlom.bluetoothmessagingapp.data.voice.AudioPlayer
import javax.inject.Inject

class PlayAudioFile @Inject constructor(
    private val audioPlayer: AudioPlayer
) {

    suspend operator fun invoke(filePath: String) =
        audioPlayer.playLocalAudio(filePath)
}
