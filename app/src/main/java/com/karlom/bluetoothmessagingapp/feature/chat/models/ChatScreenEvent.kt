package com.karlom.bluetoothmessagingapp.feature.chat.models

import com.karlom.bluetoothmessagingapp.domain.chat.models.Message

sealed interface ChatScreenEvent {


    data class OnTextChanged(val text: String) : ChatScreenEvent

    data object OnSendClicked : ChatScreenEvent

    data object OnConnectClicked : ChatScreenEvent

    data class OnSendImageClicked(val uri: String) : ChatScreenEvent

    data object OnStartRecordingVoiceClicked : ChatScreenEvent

    data object OnStopRecordingVoiceClicked : ChatScreenEvent

    data object OnDeleteVoiceRecordingClicked : ChatScreenEvent

    data class OnPlayAudioMessage(val message: Message.AudioMessage) : ChatScreenEvent

    data object OnPausePlayingAudioMessage : ChatScreenEvent
}
