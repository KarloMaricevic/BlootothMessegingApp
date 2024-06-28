package com.karlom.bluetoothmessagingapp.domain.audio

import com.karlom.bluetoothmessagingapp.data.audio.AudioPlayer
import javax.inject.Inject

class GetAudioPlayer @Inject constructor(
    private val audioPlayer: AudioPlayer
) {

    operator fun invoke() = audioPlayer
}
