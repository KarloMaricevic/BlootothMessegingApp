package com.karlom.bluetoothmessagingapp.domain.voice

import com.karlom.bluetoothmessagingapp.data.voice.VoiceRecorder
import javax.inject.Inject

class StopRecordingVoice @Inject constructor(
    private val voiceRecorder: VoiceRecorder
) {

    operator fun invoke() = voiceRecorder.endRecording()
}
