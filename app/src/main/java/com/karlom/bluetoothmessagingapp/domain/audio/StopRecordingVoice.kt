package com.karlom.bluetoothmessagingapp.domain.audio

import com.karlom.bluetoothmessagingapp.data.audio.VoiceRecorder
import javax.inject.Inject

class StopRecordingVoice @Inject constructor(
    private val voiceRecorder: VoiceRecorder
) {

    operator fun invoke() = voiceRecorder.endRecording()
}
