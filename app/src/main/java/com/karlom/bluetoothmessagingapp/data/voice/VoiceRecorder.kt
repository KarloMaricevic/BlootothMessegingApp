package com.karlom.bluetoothmessagingapp.data.voice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import arrow.core.Either
import com.karlom.bluetoothmessagingapp.core.models.Failure
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRecorder @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private companion object {
        const val RECORDER_SAMPLE_RATE = 44100
    }

    private var mediaRecorder: MediaRecorder? = null

    fun startRecording(outUri: String) =
        if (mediaRecorder != null) {
            Either.Left(Failure.ErrorMessage("Recording already in progress"))
        } else {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setAudioSamplingRate(RECORDER_SAMPLE_RATE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setOutputFile(outUri)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
                prepare()
                start()
            }
            Either.Right(outUri)
        }

    fun endRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.reset()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
